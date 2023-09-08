package de.triology.universeadm.user.imports;

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.stream.Stream;

/**
 *  CSVParser is used to parse a csv file into java beans in terms of a DTO.
 */
public interface CSVParser {

    /**
     * Parses the incoming csv file represented as io reader.
     * @param fileStream - io stream of the file
     * @return Stream containing {@link CSVUserDTO} elements. In case reader is null, an empty stream is returned.
     * @throws CsvRequiredFieldEmptyException - in case the stream cannot be prepared by parsing the header.
     */
    Stream<CSVUserDTO> parse(InputStream fileStream) throws CsvRequiredFieldEmptyException, IOException;

    /**
     * Returns the stream of all exceptions that would have been thrown during the import, but were queued.
     * The results returned by this method are not consistent until parsing is concluded.
     * @return Stream containing {@link ImportEntryResult} error elements.
     */
    Stream<ImportEntryResult> getErrors();

}
