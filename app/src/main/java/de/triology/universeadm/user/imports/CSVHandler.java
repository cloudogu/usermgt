package de.triology.universeadm.user.imports;

import com.google.inject.Inject;
import de.triology.universeadm.ConstraintViolationException;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


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

    /**
     * A list of errors that occurred when processing the import.
     */
    private final List<ImportError> errors;

    /**
     * Constructs the CSVHandler. Initializes an empty ArrayList for the errors.
     * @param userManager
     */
    @Inject
    public CSVHandler(UserManager userManager) {
        this.userManager = userManager;
        this.errors = new ArrayList<>();
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
    public Result handle(MultipartFormDataInput input) throws BadArgumentException {
        Map<String, List<InputPart>> inputParts = input.getFormDataMap();

        //only get file parts
        Optional<List<InputPart>> optFileParts = Optional.ofNullable(inputParts.get(PART_NAME));
        if (!optFileParts.isPresent()) {
            throw new BadArgumentException(String.format("unable to find parts with name \"%s\"", PART_NAME));
        }

        List<InputPart> fileParts = optFileParts.get();
        logger.debug("Received MultipartFormData with {} files", fileParts.size());

        this.validateFile(fileParts);
        logger.debug("Validated file parts in MultipartFormData");

        Reader fileReader = getFileReader(fileParts.get(0));
        logger.debug("Got reader from first file part");

        CSVParser parser = new CSVParser();
        parser.registerListener(e -> errors.add(new ImportError(ImportError.Code.PARSING_ERROR, e.getLineNumber(), e.getMessage())));

        Map<ResultType, Long> summary = parser.parse(fileReader)
                .sequential()
                .map(this::getUserPair)
                .map(Mapper::decode)
                .map(userTriple -> saveCSVImport(
                        userTriple,
                        e -> errors.add(new ImportError(ImportError.Code.VALIDATION_ERROR, userTriple.getLeft(), e.getMessage()))
                ))
                .reduce(
                        createMap(0L, 0L, (long) this.errors.size()),
                        this::accumulateResultType,
                        this::combineAccumulators
                );

        Map<ResultType, Long> finalSummary = createMap(
                summary.get(ResultType.CREATED),
                summary.get(ResultType.UPDATED),
                (long) errors.size()
        );

        Result result = new Result(finalSummary, errors);
        logger.debug("Generated CSV import result: {}", result);

        return result;
    }

    /**
     * Validates the formal correctness of the imported file.
     * @param fileParts from the MultipartForm
     * @throws BadArgumentException
     */
    private void validateFile(List<InputPart> fileParts) throws BadArgumentException {

        if (fileParts.isEmpty()) {
            throw new BadArgumentException("file part of request is empty");
        }

        if (fileParts.size() > 1) {
            throw new BadArgumentException("only one file may be uploaded with one request");
        }

        InputPart filePart = fileParts.get(0);
        String filename = this.getFileName(filePart);

        if (filename.isEmpty()){
            throw new BadArgumentException("invalid or empty filename in Content-Disposition");
        }

        if (!filename.endsWith(".csv")){
            throw new BadArgumentException(String.format("Unsupported filetype \"%s\"",
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
     * @throws BadArgumentException
     */
    private Reader getFileReader(InputPart file) throws BadArgumentException {
        BufferedReader inputReader;

        try {
            InputStream inputStream = file.getBody(InputStream.class, null);
            inputReader = new BufferedReader(new InputStreamReader(inputStream));

            return inputReader;
        } catch (IOException | NullPointerException e) {
            throw new BadArgumentException(
                    "unable to parse file",
                    "could not convert multipart file to InputStream",
                    e
            );
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
     * @param userTriple consisting of:
     * <ul>
     *  <li>Long: Line number within the csv file</li>
     *  <li>Boolean: Flag indication whether the user needs to be created</li>
     *  <li>User: User object to be saved / modified</li>
     * </ul>
     *
     * @param listener that will be notified when ConstraintViolationException occurs
     * <p>
     * @return ResultType whether the user has been created, updated or skipped
     */
    private ResultType saveCSVImport(Triple<Long, Boolean, User> userTriple, ExceptionListener<ConstraintViolationException> listener) {
        try {
            if (userTriple.getMiddle()) {
                this.userManager.create(userTriple.getRight());
                return ResultType.CREATED;
            } else {
                this.userManager.modify(userTriple.getRight());
                return ResultType.UPDATED;
            }
        } catch (ConstraintViolationException e) {
            listener.notify(e);
        }

        return ResultType.SKIPPED;
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
    private EnumMap<ResultType, Long> accumulateResultType(EnumMap<ResultType, Long> partialAcc, ResultType next) {
        switch (next) {
            case CREATED:
                return createMap(
                        partialAcc.get(ResultType.CREATED) +1,
                        partialAcc.get(ResultType.UPDATED),
                        (long) errors.size()
                );
            case UPDATED:
                return createMap(
                        partialAcc.get(ResultType.CREATED),
                        partialAcc.get(ResultType.UPDATED) + 1,
                        (long) errors.size()
                );
            default:
                return createMap(
                        partialAcc.get(ResultType.CREATED),
                        partialAcc.get(ResultType.UPDATED),
                        (long) errors.size()
                );
        }
    }

    /**
     * Combiner used within the reduce function as the function itself is used with different types.
     * @param partialAcc - of stream 1
     * @param partialAcc2 - of stream 2
     * @return EnumMap<ResultType, Long> as final result for different streams
     */
    private EnumMap<ResultType, Long> combineAccumulators(EnumMap<ResultType, Long> partialAcc, EnumMap<ResultType, Long> partialAcc2) {
        return createMap(
                partialAcc.get(ResultType.CREATED) + partialAcc2.get(ResultType.CREATED),
                partialAcc.get(ResultType.UPDATED) + partialAcc2.get(ResultType.UPDATED),
                partialAcc.get(ResultType.SKIPPED) + partialAcc2.get(ResultType.SKIPPED)
        );
    }


}
