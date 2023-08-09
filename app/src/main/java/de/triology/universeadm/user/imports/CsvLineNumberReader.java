package de.triology.universeadm.user.imports;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.Reader;

public class CsvLineNumberReader extends CSVReader {
    public CsvLineNumberReader(Reader reader) {
        super(reader);
    }

    @Override
    public String[] readNext() throws IOException, CsvValidationException {
        String[] nextLine = super.readNext();
        return nextLine == null ? null :
                ArrayUtils.add(nextLine, String.valueOf(this.linesRead));
    }

    @Override
    public String[] readNextSilently() throws IOException {
        String[] nextLine = super.readNextSilently();
        return nextLine == null ? null :
                ArrayUtils.add(nextLine, "LINE_NUMBER");
    }

}
