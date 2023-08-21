package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.bean.exceptionhandler.ExceptionHandlerThrow;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *  CSVParser is used to parse a csv file into java beans in terms of a DTO.
 */
public class CSVParser {

    /**
     * ExceptionListener is used to inform the calling entity about any CsvExceptions that may occur.
     */
    private ExceptionListener<CsvException> exceptionListener;

    private CsvToBean<CSVUserDTO> csv2bean;

    /**
     * Fallback ExceptionHandler in case no listener is provided.
     */
    private final CsvExceptionHandler csvExceptionHandler = new ExceptionHandlerThrow();

    private static final Logger logger =
            LoggerFactory.getLogger(CSVParser.class);

    /**
     * Registers the exception listener so it can be used.
     * @param listener FunctionalInterface to notify about CsvException
     */
    public void registerListener(ExceptionListener<CsvException> listener) {
        this.exceptionListener = listener;
    }

    /**
     * Parses the incoming csv file represented as io reader.
     * @param reader - io Reader of the file
     * @return Stream containing {@link CSVUserDTO} elements. In case reader is null, an empty stream is returned.
     * @throws BadArgumentException - in case the stream cannot be prepared.
     */
    public Stream<CSVUserDTO> parse(Reader reader) throws BadArgumentException {

        if (reader == null) {
            throw new BadArgumentException("invalid csv file", "Input reader is null");
        }

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
     * Handles an occurring CsvException by notifying the {@link ExceptionListener}. When no ExceptionListener is
     * registered, there is a fallback to {@link ExceptionHandlerThrow}, that throws the exception.
     * @param e - CsvException occurred while parsing a single row.
     * @return CsvException is declared to satisfy the interface {@link CsvExceptionHandler} but there is no value
     * to be returned.
     * @throws CsvException - in case of an invalid row
     */
    private CsvException handleException(CsvException e) throws CsvException {
        logger.warn("Received CsvException while parsing CSV file");

        Optional<ExceptionListener<CsvException>> listener = Optional.ofNullable(this.exceptionListener);

        if (listener.isPresent()) {
            listener.get().notify(e);

            return null;
        } else {
            return csvExceptionHandler.handleException(e);
        }
    }

    /**
     * Prepares the stream by calling the supplier function for the csv stream. Doing this can cause a
     * CsvException wrapped inside a RuntimeException when the header row of the csv cannot be parsed.
     * @param csvStream - supplier to get the stream
     * @return Stream containing {@link CSVUserDTO} elements
     * @throws BadArgumentException - wraps the CsvException
     */
    private Stream<CSVUserDTO> prepareStream(Supplier<Stream<CSVUserDTO>> csvStream) throws BadArgumentException {
        try {
            return csvStream.get();
        } catch (RuntimeException e) {
            logger.warn("Received RuntimeException while getting CSV stream");

            Throwable cause = e.getCause();

            if (!(cause instanceof CsvException)) {
                // Rethrow exception as it is not expected.
                throw e;
            }

            // wrap CsvException in BadArgumentException instead of RuntimeException
            throw new BadArgumentException(e.getMessage(), cause);
        }
    }

}
