package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvBindByName;

/**
 * CSVRecord is the base class for any DTO bean that is produced. It adds the line number of the row within the csv
 * file to the DTO.
 */
public abstract class CSVRecord {

    /**
     * Line number within the csv file.
     */
    @CsvBindByName(column = "line_number")
    private Long lineNumber;

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
