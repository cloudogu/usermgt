package de.triology.universeadm.user.imports;

import java.util.List;
import java.util.Map;

/**
 * The result of a csv import.
 * <p>
 * It contains a summary of the processed rows in terms of users
 * created, updated or skipped. For further information, errors are
 * provided for the skipped rows.
 */
public class Result {

    private final Map<ResultType, Long> summary;
    private final List<ImportError> errors;

    public Result(Map<ResultType, Long> summary, List<ImportError> errors) {
        this.summary = summary;
        this.errors = errors;
    }

    public Map<ResultType, Long> getSummary() {
        return summary;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "Result{" +
                "summary=" + summary +
                ", errors=" + errors +
                '}';
    }
}
