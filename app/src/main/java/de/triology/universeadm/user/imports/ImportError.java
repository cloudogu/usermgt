package de.triology.universeadm.user.imports;

import com.google.common.collect.ImmutableMap;
import org.omg.CORBA.INTERNAL;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        FIELD_ASSIGNMENT_ERROR(103),
        FIELD_LENGTH_ERROR(104),
        VALIDATION_ERROR(200),
        UNIQUE_FIELD_ERROR(201),
        FIELD_FORMAT_ERROR(202),
        MISSING_REQUIRED_FIELD_ERROR(204),
        INTERNAL_ERROR(300),
        WRITE_RESULT_ERROR(301);

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

    private final Map<String, List<String>> params;

    /**
     * Constructs an ImportError
     * @param code - enum value, so only predefined codes can be used
     * @param lineNumber - affected csv row
     * @param message - error message to describe the error
     */
    private ImportError(int code, long lineNumber, String message, Map<String, List<String>> params) {
        this.errorCode = code;
        this.lineNumber = lineNumber;
        this.message = message;
        this.params = params;
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

    public Map<String, List<String>> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportError that = (ImportError) o;
        return errorCode == that.errorCode && lineNumber == that.lineNumber && Objects.equals(message, that.message) && Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, lineNumber, message, params);
    }

    @Override
    public String toString() {
        return "ImportError{" +
                "errorCode=" + errorCode +
                ", lineNumber=" + lineNumber +
                ", message='" + message + '\'' +
                ", params=" + params +
                '}';
    }

    public static class Builder {
        private final int errorCode;
        private long lineNumber = -1;
        private String message = "";
        private List<String> affectedColumns = Collections.emptyList();

        public Builder(ImportError.Code code) {
            this.errorCode = code.value;
        }

        public Builder withLineNumber(long lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder withErrorMessage(String msg) {
            this.message = msg;
            return this;
        }

        public Builder withAffectedColumns(List<String> columns) {
            this.affectedColumns = columns;
            return this;
        }

        public ImportError build() {
            if (this.affectedColumns.isEmpty()) {
                return new ImportError(this.errorCode, this.lineNumber, this.message, null);
            }

            Map<String, List<String>> params = ImmutableMap.<String, List<String>>builder()
                    .put("columns", this.affectedColumns)
                    .build();

            return new ImportError(this.errorCode, this.lineNumber, this.message, params);
        }
    }
}
