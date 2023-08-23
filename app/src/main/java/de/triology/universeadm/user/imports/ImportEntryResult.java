package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.User;

public class ImportEntryResult {
    private final ResultType resultType;
    private ImportError importError = null;
    private User user = null;

    public static ImportEntryResult skipped(ImportError importError) {
        return new ImportEntryResult(ResultType.SKIPPED, importError);
    }

    public static ImportEntryResult created(User user) {
        return new ImportEntryResult(ResultType.CREATED, user);
    }

    public static ImportEntryResult updated(User user) {
        return new ImportEntryResult(ResultType.UPDATED, user);
    }

    public ImportEntryResult(ResultType resultType, ImportError importError) {
        this.resultType = resultType;
        this.importError = importError;
    }

    public ImportEntryResult(ResultType resultType, User user) {
        this.resultType = resultType;
        this.user = user;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public ImportError getImportError() {
        return importError;
    }

    public User getUser() {
        return user;
    }
}
