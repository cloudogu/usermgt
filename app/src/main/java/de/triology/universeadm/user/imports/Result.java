package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The result of a csv import.
 * <p>
 * It contains a summary of the processed rows in terms of users
 * created, updated or skipped. For further information, errors are
 * provided for the skipped rows.
 */
public class Result {
    private final UUID importID;
    private final String filename;
    private final long timestamp;
    private final List<User> created;
    private final List<User> updated;
    private final List<ImportError> errors;

    public Result(UUID importID, String filename) {
        this.importID = importID;
        this.filename = filename;
        this.timestamp = System.currentTimeMillis();
        this.created = new ArrayList<>();
        this.updated = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public Result(UUID id, String filename, List<User> created, List<User> updated, List<ImportError> errors) {
        this.importID = id;
        this.filename = filename;
        this.timestamp = System.currentTimeMillis();
        this.created = created;
        this.updated = updated;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "Result{" +
                "importID=" + importID +
                ", filename='" + filename + '\'' +
                ", timestamp=" + timestamp +
                ", created=" + created +
                ", updated=" + updated +
                ", errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return timestamp == result.timestamp && Objects.equals(importID, result.importID) && Objects.equals(filename, result.filename) && Objects.equals(created, result.created) && Objects.equals(updated, result.updated) && Objects.equals(errors, result.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importID, filename, timestamp, created, updated, errors);
    }

    public UUID getImportID() {
        return importID;
    }

    public String getFilename() {
        return filename;
    }

    public long getTimestamp() {
        return timestamp;
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

    public Summary getSummary() {
        int createdSize = this.created.size();
        int updatedSize = this.updated.size();
        int skippedSize = this.errors.stream()
                .map(ImportError::getLineNumber)
                .collect(Collectors.toSet())
                .size();

        return new Summary(this.importID, this.filename, this.timestamp, createdSize, updatedSize, skippedSize);
    }

    public static class Summary {
        private final UUID importID;
        private final String filename;
        private final long timestamp;
        private final Map<String, Integer> summary;

        public Summary(UUID importID, String filename, long timestamp, int created, int updated, int skipped) {
            this.importID = importID;
            this.filename = filename;
            this.timestamp = timestamp;

            this.summary = new LinkedHashMap<>();
            this.summary.put("created", created);
            this.summary.put("updated", updated);
            this.summary.put("skipped", skipped);
        }

        public UUID getImportID() {
            return importID;
        }

        public String getFilename() {
            return filename;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Map<String, Integer> getSummary() {
            return summary;
        }
    }
}
