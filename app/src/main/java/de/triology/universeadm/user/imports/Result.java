package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a csv import.
 * <p>
 * It contains a summary of the processed rows in terms of users
 * created, updated or skipped. For further information, errors are
 * provided for the skipped rows.
 */
public class Result {
    private List<User> created = new ArrayList<>();
    private List<User> updated = new ArrayList<>();
    private List<ImportError> errors = new ArrayList<>();

    public Result() {}

    public Result(List<ImportError> errors) {
        this.errors = errors;
    }

    public Result(List<User> created, List<User> updated, List<ImportError> errors) {
        this.created = created;
        this.updated = updated;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "Result{" +
                ", created=" + created +
                ", update=" + updated +
                ", errors=" + errors +
                '}';
    }

    public List<User> getCreated() {
        return created;
    }

    public List<User> getUpdated() {
        return updated;
    }

    public List<ImportError> getErrors() {
        return errors;
    }
}
