package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvBindByName;

public abstract class CSVRecord {

    @CsvBindByName(column = "line_number")
    private Long lineNumber;

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
