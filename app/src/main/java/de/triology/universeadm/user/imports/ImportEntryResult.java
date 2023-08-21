package de.triology.universeadm.user.imports;

public class ImportEntryResult {
    private final ResultType resultType;
    private final ImportError importError;

    public static ImportEntryResult Skipped(ImportError importError) {
        return new ImportEntryResult(ResultType.SKIPPED, importError);
    }

    public ImportEntryResult(ResultType resultType) {
        this(resultType, null);
    }

    public ImportEntryResult(ResultType resultType, ImportError importError) {
        this.resultType = resultType;
        this.importError = importError;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public ImportError getImportError() {
        return importError;
    }
}
