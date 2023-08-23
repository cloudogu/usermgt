package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.Reader;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class CSVParserImpl implements CSVParser {

    private CsvToBean<CSVUserDTO> csv2bean;

    private static final Logger logger =
            LoggerFactory.getLogger(CSVParserImpl.class);


    public Stream<CSVUserDTO> parse(@NotNull Reader reader) throws MissingHeaderFieldException {

        CsvToBean<CSVUserDTO> beans = new CsvToBeanBuilder<CSVUserDTO>(new CsvLineNumberReader(reader))
                .withType(CSVUserDTO.class)
                .withIgnoreEmptyLine(true)
                .withThrowExceptions(false)
                .build();

        this.csv2bean = beans;

        logger.debug("Build CsvToBean instance for CSVUserDTO");

        return this.prepareStream(beans::stream);
    }

    public Stream<ImportEntryResult> getErrors() {
        return Optional.ofNullable(csv2bean)
                .map(CsvToBean::getCapturedExceptions)
                .map(Collection::stream)
                .map(csvExceptionStream -> csvExceptionStream
                        // TODO: Add CSVException - ImportError Mapper
                        .map(e -> ImportEntryResult.Skipped(new ImportError(ImportError.Code.PARSING_ERROR, e.getLineNumber(), e.getMessage()))))
                .orElse(Stream.empty());
    }

    /**
     * Prepares the stream by calling the supplier function for the csv stream. Doing this can cause a
     * CsvException wrapped inside a RuntimeException when the header row of the csv cannot be parsed.
     * @param csvStream - supplier to get the stream
     * @return Stream containing {@link CSVUserDTO} elements
     * @throws BadArgumentException - wraps the CsvException
     */
    private Stream<CSVUserDTO> prepareStream(Supplier<Stream<CSVUserDTO>> csvStream) throws MissingHeaderFieldException {
        try {
            return csvStream.get();
        } catch (RuntimeException e) {
            logger.warn("Received RuntimeException while getting CSV stream");

            Throwable cause = e.getCause();

            if (!(cause instanceof CsvException)) {
                // Rethrow exception as it is not expected.
                throw e;
            }

            if (!(cause instanceof CsvRequiredFieldEmptyException)) {
                // Rethrow exception as it is not expected.
                throw e;
            }

            // wrap CsvException in MissingHeaderFieldException instead of RuntimeException
            throw new MissingHeaderFieldException(e.getMessage(), cause);
        }
    }

}
