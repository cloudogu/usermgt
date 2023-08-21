package de.triology.universeadm.user.imports;

class ImportEntryResult {
    public ResultType resultType;
    public ImportError importError;

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
}
