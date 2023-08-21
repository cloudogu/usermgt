package de.triology.universeadm.user.imports;

/**
 * ImportError is an error occurred during the import. The occurrence of the error does not
 * prevent further execution of the import, but can be collected as values.
 */
public class ImportError {

    /**
     *  Enum of the error codes that can be used for the ImportError
     */
    public enum Code {
        PARSING_ERROR(100),
        FIELD_CONVERSION_ERROR(101),
        MISSING_FIELD_ERROR(102),
        VALIDATION_ERROR(200),
        UNIQUE_FIELD_ERROR(201),
        FIELD_FORMAT_ERROR(202);

        public final int value;

        Code(int value) {
            this.value = value;
        }
    }

    /**
     * errorCode represented as int value
     */
    private final int errorCode;
    /**
     * lineNumber of the csv row in which the ImportError occurred
     */
    private final long lineNumber;
    /**
     * error message describing the reason why the ImportError occurred
     */
    private final String message;

    /**
     * Constructs an ImportError
     * @param code - enum value, so only predefined codes can be used
     * @param lineNumber - affected csv row
     * @param message - error message to describe the error
     */
    public ImportError(Code code, long lineNumber, String message) {
        this.errorCode = code.value;
        this.lineNumber = lineNumber;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }
}
