package de.triology.universeadm.user.imports;

import com.google.inject.Inject;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.triology.universeadm.ConstraintViolationException;
import de.triology.universeadm.mapping.IllegalQueryException;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.opensaml.artifact.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;


/**
 *  Handles the data provided by the csv import.
 * <p>
 *  It uses the UserManager to retrieve and store users during the import.
 *  ImportErrors that have been occurred during the import are stored in errors.
 *  Because of this CSVHandler is a stateful component that needs be initialized with every import.
 *
 */
public class CSVHandler {

    private static final Logger logger = LoggerFactory.getLogger(CSVHandler.class);
    private static final String PART_NAME = "file";

    /**
     * UserManager to retrieve existing users and store users generated through the import.
     */
    private final UserManager userManager;

    private final CSVParser csvParser;

    /**
     * Constructs the CSVHandler. Initializes an empty ArrayList for the errors.
     * @param userManager
     */
    @Inject
    public CSVHandler(UserManager userManager, CSVParser csvParser) {
        this.userManager = userManager;
        this.csvParser = csvParser;
    }

    /**
     * Handles the MultipartFormDataInput data provided by the user request.
     * <p>
     * It converts the information provided by the csv file and stores or modifies it in the repository.
     * The MultipartFormDataInput is processed within a stream.
     * @param input as MultipartFormDataInput
     * @return Result containing information how many lines (Users) are created, modified or skipped. In terms of
     * skipped rows, errors are provided as well.
     * @throws BadArgumentException in case further processing of the csv file is not possible. Throwing the exception
     * means no data has been processed.
     */
    public Result handle(MultipartFormDataInput input) throws MissingHeaderFieldException {
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

        Reader fileReader = getFileReader(fileParts.get(0));
        logger.debug("Got reader from first file part");
        List<ImportError> validationErrors = new ArrayList<>();
        List<ImportError> parsingErrors = new ArrayList<>();


        Stream<CSVUserDTO> parsedDataStream;
        try {
            parsedDataStream = this.csvParser.parse(fileReader);
        } catch (MissingHeaderFieldException exp) {
            if (exp.getCause() instanceof CsvRequiredFieldEmptyException) {
                CsvRequiredFieldEmptyException csvExp = (CsvRequiredFieldEmptyException) exp.getCause();
                validationErrors.add(new ImportError(ImportError.Code.MISSING_FIELD_ERROR, csvExp.getLineNumber(), csvExp.getMessage()));
                return new Result(validationErrors);
            }
            throw exp;
        }

        Stream<ImportEntryResult> parsingResults = parsingErrors.stream().map(ImportEntryResult::skipped);
        Stream<ImportEntryResult> results = parsedDataStream
                .sequential()
                .map(this::getUserPair) // load user from LDAP
                .map(Mapper::decode) // add more information
                .map(userTriple -> {
                    ImportEntryResult partialResult = saveCSVImport(userTriple.getLeft(), userTriple.getMiddle(), userTriple.getRight());
                    if (partialResult.getImportError() != null){
                        validationErrors.add(partialResult.getImportError());
                    }
                    return partialResult;
                });

        Stream<ImportEntryResult> finalResultStream = Stream.concat(parsingResults, results);
        Result finalResult = finalResultStream.reduce(
                        new Result(),
                        this::accumulateResultType,
                        this::combineAccumulators
                );

//        Result result = new Result(validationErrors);
        logger.debug("Generated CSV import result: {}", finalResult);

        return finalResult;
    }

    /**
     * Validates the formal correctness of the imported file.
     * @param fileParts from the MultipartForm
     * @throws BadArgumentException
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

        if (filename.isEmpty()){
            throw new InvalidArgumentException("invalid or empty filename in Content-Disposition");
        }

        if (!filename.endsWith(".csv")){
            throw new InvalidArgumentException(String.format("Unsupported filetype \"%s\"",
                    filename.substring(filename.lastIndexOf("."))
            ));
        }
    }

    /**
     * Returns the filename from MultipartForm header stored within Content-Disposition.
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
     * @param file uploaded be the user.
     * @return Reader (= BufferedReader)
     * @throws InvalidArgumentException
     */
    private Reader getFileReader(@NotNull InputPart file) throws InvalidArgumentException {
        BufferedReader inputReader;

        try {
            InputStream inputStream = file.getBody(InputStream.class, null);
            inputReader = new BufferedReader(new InputStreamReader(inputStream));

            return inputReader;
        } catch (IOException e) {
            logger.error(e.toString());
            throw new InvalidArgumentException("unable to parse file");
        }
    }

    /**
     * The user DTO generated from the csv file is used to check whether the user already exists.
     * @param userDTO from csv file.
     * @return Pair containing the potential existing user and the user dto.
     */
    private Pair<Optional<User>, CSVUserDTO> getUserPair(CSVUserDTO userDTO) {
        User user = userManager.get(userDTO.getUsername());
        return Pair.of(Optional.ofNullable(user), userDTO);
    }

    /**
     * Saves or modifies the users within the repository.
     * @param isNewUser: Flag indication whether the user needs to be created
     * @param user: User object to be saved / modified
     * <p>
     * @return ResultType whether the user has been created, updated or skipped
     */
    private ImportEntryResult saveCSVImport(long lineNumber, Boolean isNewUser, User user) {
        try {
            if (isNewUser) {
                this.userManager.create(user);
                return ImportEntryResult.created(user);
            } else {
                this.userManager.modify(user);
                return ImportEntryResult.updated(user);
            }
        } catch (IllegalQueryException e) {
            return ImportEntryResult.skipped(new ImportError(ImportError.Code.PARSING_ERROR, lineNumber, e.getMessage()));
        } catch (ConstraintViolationException e) {
            return new ImportEntryResult(ResultType.SKIPPED, new ImportError(ImportError.Code.VALIDATION_ERROR, lineNumber, e.getMessage()));
        }
    }

    /**
     * Creates a map with a summary of the import process
     * @param created users
     * @param updated users
     * @param skipped users
     * @return EnumMap<ResultType, Long> with the amount of users that have been created, updated or skipped.
     */
    private EnumMap<ResultType, Long> createMap(Long created, Long updated, Long skipped) {
        EnumMap<ResultType, Long> summary = new EnumMap<>(ResultType.class);
        summary.put(ResultType.CREATED, created);
        summary.put(ResultType.UPDATED, updated);
        summary.put(ResultType.SKIPPED, skipped);

        return summary;
    }

    /**
     * Accumulator to increment the number for each ResultType
     * @param partialAcc - Partial summary of the import process
     * @param next - next result of an import
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
     * @param partialAcc - of stream 1
     * @param partialAcc2 - of stream 2
     * @return EnumMap<ResultType, Long> as final result for different streams
     */
    private Result combineAccumulators(Result partialAcc, Result partialAcc2) {
        partialAcc.getCreated().addAll(partialAcc2.getCreated());
        partialAcc.getUpdated().addAll(partialAcc2.getUpdated());
        partialAcc.getErrors().addAll(partialAcc2.getErrors());

        return new Result(partialAcc.getCreated(), partialAcc.getUpdated(), partialAcc.getErrors());
    }


}
