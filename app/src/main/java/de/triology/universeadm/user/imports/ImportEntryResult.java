package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.User;

public class ImportEntryResult {
    private final ResultType resultType;
    private final ImportError importError;
    private final User user;

    public static ImportEntryResult skipped(ImportError importError) {
        return new ImportEntryResult(ResultType.SKIPPED, importError, null);
    }

    public static ImportEntryResult created(User user) {
        return new ImportEntryResult(ResultType.CREATED, null, user);
    }

    public static ImportEntryResult updated(User user) {
        return new ImportEntryResult(ResultType.UPDATED, null, user);
    }

    private ImportEntryResult(ResultType resultType, ImportError importError, User user) {
        this.resultType = resultType;
        this.importError = importError;
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
