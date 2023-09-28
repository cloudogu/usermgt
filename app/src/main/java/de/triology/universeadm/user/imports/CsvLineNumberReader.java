package de.triology.universeadm.user.imports;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * CsvLineNumberReader is an extension of the CSVReader
 * that provides the line number of a record within the csv file.
 *
 * @see <a href="https://stackoverflow.com/questions/12357163/opencsv-find-out-line-number">stack overflow</a>
 */
public class CsvLineNumberReader extends CSVReader {

    public static final String LINE_COLUMN = "LINE_NUMBER";

    private final Consumer<String[]> headerCallback;

    public CsvLineNumberReader(Reader reader, Consumer<String[]> headerCallback) {
        super(reader);
        this.headerCallback = headerCallback;
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
        nextLine = nextLine == null ? null : ArrayUtils.add(nextLine, LINE_COLUMN);

        if (this.headerCallback != null) {
            this.headerCallback.accept(nextLine);
        }

        return nextLine;
    }

}
