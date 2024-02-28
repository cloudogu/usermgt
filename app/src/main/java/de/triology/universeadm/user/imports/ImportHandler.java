package de.triology.universeadm.user.imports;

import com.google.inject.Inject;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.triology.universeadm.Constraint;
import de.triology.universeadm.UniqueConstraintViolationException;
import de.triology.universeadm.mail.MailService;
import de.triology.universeadm.mapping.IllegalQueryException;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.opensaml.artifact.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Handles the data provided by the csv import.
 * <p>
 * It uses the UserManager to retrieve and store users during the import.
 * ImportErrors that have been occurred during the import are part of the Result.
 */
public class ImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(ImportHandler.class);
    private static final String PART_NAME = "file";

    /**
     * UserManager to retrieve existing users and store users generated through the import.
     */
    private final UserManager userManager;

    /**
     * CSVParser to parse the csv file to DTOs
     */
    private final CSVParser csvParser;

    /**
     * ResultRepository persists the result of the csv import.
     */
    private final ResultRepository resultRepository;

    /**
     * ResultRepository get the summaries of the csv imports.
     */
    private final SummaryRepository summaryRepository;
    /**
     * Mail service notifies newly created users about their username and password.
     */
    private final MailService mailService;

    /**
     * Constructs the CSVHandler.
     *
     * @param userManager      - injected
     * @param csvParser        - injected
     * @param resultRepository - injected
     * @param mailService      - injected
     */
    @Inject
    public ImportHandler(UserManager userManager, CSVParser csvParser, ResultRepository resultRepository, SummaryRepository summaryRepository, MailService mailService) {
        this.userManager = userManager;
        this.csvParser = csvParser;
        this.resultRepository = resultRepository;
        this.summaryRepository = summaryRepository;
        this.mailService = mailService;
    }

    /**
     * Handles the MultipartFormDataInput data provided by the user request.
     * <p>
     * It converts the information provided by the csv file and stores or modifies it in the repository.
     * The MultipartFormDataInput is processed within a stream.
     *
     * @param input as MultipartFormDataInput
     * @return Result containing information how many lines (Users) are created, modified or skipped. In terms of
     * skipped rows, errors are provided as well.
     * @throws InvalidArgumentException in case further processing of the csv file is not possible.
     * Throwing the exception means no data has been processed.
     */
    public Result handle(MultipartFormDataInput input) throws CsvRequiredFieldEmptyException, IOException {
        Map<String, List<InputPart>> inputParts = input.getFormDataMap();

        //only get file parts
        Optional<List<InputPart>> optFileParts = Optional.ofNullable(inputParts.get(PART_NAME));
        if (!optFileParts.isPresent()) {
            throw new InvalidArgumentException(String.format("unable to find parts with name \"%s\"", PART_NAME));
        }

        List<InputPart> fileParts = optFileParts.get();
        logger.debug("Received MultipartFormData with {} files", fileParts.size());

        this.validateFile(fileParts);
        logger.debug("Validated file parts in MultipartFormData");

        InputPart filePart = fileParts.get(0);

        InputStream fileInputStream = getInputStream(filePart);
        logger.debug("Got reader from first file part");

        UUID importID = UUID.randomUUID();
        logger.debug("Created ImportID with UUID {}", importID);

        List<ImportEntryResult> results = this.csvParser.parse(fileInputStream)
                .sequential()
                .map(this::getUserPair) // load user from LDAP
                .map(Mapper::decode) // add more information
                .map(userTriple -> saveCSVImport(userTriple.getLeft(), userTriple.getMiddle(), userTriple.getRight()))
                .collect(Collectors.toList());

        results.addAll(csvParser.getErrors().collect(Collectors.toList()));
        Result finalResult = results.stream().reduce(
                new Result(importID, this.getFileName(filePart)),
                this::accumulateResultType,
                this::combineAccumulators
        );

        logger.debug("Generated CSV import result: {}", finalResult);

        Optional<ImportError> optWriteError = this.writeResult(finalResult);
        optWriteError.ifPresent(importError -> {
            logger.debug("Received WriteError for result - add Error to result and return it");
            finalResult.getErrors().add(importError);
        });

        return finalResult;
    }

    /**
     * Validates the formal correctness of the imported file.
     *
     * @param fileParts from the MultipartForm
     * @throws InvalidArgumentException
     */
    private void validateFile(List<InputPart> fileParts) throws InvalidArgumentException {

        if (fileParts.isEmpty()) {
            throw new InvalidArgumentException("file part of request is empty");
        }

        if (fileParts.size() > 1) {
            throw new InvalidArgumentException("only one file may be uploaded with one request");
        }

        InputPart filePart = fileParts.get(0);
        String filename = this.getFileName(filePart);

        if (filename.isEmpty()) {
            throw new InvalidArgumentException("invalid or empty filename in Content-Disposition");
        }

        if (!filename.endsWith(".csv")) {
            throw new InvalidArgumentException(String.format("Unsupported filetype \"%s\"",
                    filename.substring(filename.lastIndexOf("."))
            ));
        }
    }

    /**
     * Returns the filename from MultipartForm header stored within Content-Disposition.
     *
     * @param filePart containing the header.
     * @return filename of the uploaded file.
     */
    private String getFileName(InputPart filePart) {
        return Arrays.stream(filePart
                        .getHeaders()
                        .getFirst("Content-Disposition")
                        .split(";"))
                .map(String::trim)
                .filter(name -> name.startsWith("filename"))
                .map(name -> name.split("=")[1].trim().replaceAll("\"", ""))
                .findFirst()
                .orElse("");
    }

    /**
     * Returns the reader used to read the uploaded file.
     * For efficient reading a BufferedReader is used.
     *
     * @param file uploaded be the user.
     * @return InputStream - from file request
     * @throws InvalidArgumentException
     */
    private InputStream getInputStream(@NotNull InputPart file) {
        try {
            return file.getBody(InputStream.class, null);
        } catch (IOException e) {
            logger.error(e.toString());
            throw new InvalidArgumentException("unable to get body from file");
        }
    }

    /**
     * The user DTO generated from the csv file is used to check whether the user already exists.
     *
     * @param userDTO from csv file.
     * @return Pair containing the potential existing user and the user dto.
     */
    private Pair<Optional<User>, CSVUserDTO> getUserPair(CSVUserDTO userDTO) {
        User user = userManager.get(userDTO.getUsername());
        return Pair.of(Optional.ofNullable(user), userDTO);
    }

    /**
     * Saves or modifies the users within the repository.
     *
     * @param isNewUser: Flag indication whether the user needs to be created
     * @param user:      User object to be saved / modified
     * <p>
     * @return ResultType whether the user has been created, updated or skipped
     */
    private ImportEntryResult saveCSVImport(long lineNumber, Boolean isNewUser, User user) {
        try {
            if (isNewUser) {
                this.userManager.create(user);
                this.mailService.notify(user);
                return ImportEntryResult.created(user);
            } else {
                this.userManager.modify(user);
                return ImportEntryResult.updated(user);
            }
        } catch (IllegalQueryException e) {
            ImportError error = new ImportError.Builder(ImportError.Code.VALIDATION_ERROR)
                    .withErrorMessage(e.getMessage())
                    .withLineNumber(lineNumber)
                    .build();

            return ImportEntryResult.skipped(error);
        } catch (UniqueConstraintViolationException e) {
            ImportError error = new ImportError.Builder(ImportError.Code.UNIQUE_FIELD_ERROR)
                    .withErrorMessage(e.getMessage())
                    .withLineNumber(lineNumber)
                    .withAffectedColumns(mapConstraintToColumn(e.violated))
                    .build();

            return ImportEntryResult.skipped(error);
        } catch (ConstraintViolationException e) {
                List<ConstraintViolation<?>> violas = new ArrayList<>(e.getConstraintViolations());
                List<String> strViolas = violas.stream().map(ConstraintViolation::getPropertyPath).map(Path::toString).collect(Collectors.toList());
                ImportError error = new ImportError.Builder(ImportError.Code.FIELD_FORMAT_ERROR)
                        .withErrorMessage(e.getMessage())
                        .withLineNumber(lineNumber)
                        .withAffectedColumns(strViolas)
                        .build();

                return ImportEntryResult.skipped(error);
        }
    }

    private List<String> mapConstraintToColumn(Constraint.ID[] constraints) {
        if (constraints.length < 1) {
            return Collections.emptyList();
        }
        List<String> violatedColumnConstraints = new ArrayList<>();
        for (Constraint.ID constraint : constraints) {
            switch (constraint) {
                case UNIQUE_EMAIL:
                    violatedColumnConstraints.add("mail");
                    break;
                case UNIQUE_USERNAME:
                    violatedColumnConstraints.add("username");
                    break;
            }
        }
        return violatedColumnConstraints;
    }

    /**
     * Accumulator to increment the number for each ResultType
     *
     * @param partialAcc - Partial summary of the import process
     * @param next       - next result of an import
     * @return EnumMap<ResultType, Long> as final summary
     */
    private Result accumulateResultType(Result partialAcc, ImportEntryResult next) {
        switch (next.getResultType()) {
            case CREATED:
                partialAcc.getCreated().add(next.getUser());
                return partialAcc;
            case UPDATED:
                partialAcc.getUpdated().add(next.getUser());
                return partialAcc;
            case SKIPPED:
                partialAcc.getErrors().add(next.getImportError());
                return partialAcc;
        }
        throw new UnsupportedOperationException(String.format("operation '%s' is not supported", next.getResultType()));
    }

    /**
     * Combiner used within the reduce function as the function itself is used with different types.
     *
     * @param partialAcc  - of stream 1
     * @param partialAcc2 - of stream 2
     * @return EnumMap<ResultType, Long> as final result for different streams
     */
    private Result combineAccumulators(Result partialAcc, Result partialAcc2) {
        partialAcc.getCreated().addAll(partialAcc2.getCreated());
        partialAcc.getUpdated().addAll(partialAcc2.getUpdated());
        partialAcc.getErrors().addAll(partialAcc2.getErrors());

        return new Result(partialAcc.getImportID(), partialAcc.getFilename(), partialAcc.getCreated(), partialAcc.getUpdated(), partialAcc.getErrors());
    }

    private Optional<ImportError> writeResult(Result result) {
        try {
            this.resultRepository.write(result);
        } catch (IOException e) {
            logger.warn("Could not write Result", e);

            return Optional.of(new ImportError.Builder(ImportError.Code.WRITE_RESULT_ERROR)
                    .withErrorMessage("Unable to save result to repository")
                    .build());
        }

        return Optional.empty();
    }

    public Result getResult(UUID importID) throws FileNotFoundException {
       return this.resultRepository.read(importID);
    }

    public Pair<List<Result.Summary>, Integer> getSummaries(int start, int limit) throws IOException {
        List<Result.Summary> allSummaries = this.summaryRepository.getSummaries();
        List<Result.Summary> paginatedList = allSummaries.stream()
                .sorted(Comparator.comparingLong(Result.Summary::getTimestamp).reversed())
                .skip(start)
                .limit(limit)
                .collect(Collectors.toList());

        return Pair.of(paginatedList, allSummaries.size());
    }

    public boolean deleteResult(UUID importID) throws IOException {
        return this.resultRepository.delete(importID);
    }

}
