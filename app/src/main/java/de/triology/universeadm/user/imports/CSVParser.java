package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CSVParser {

    private ExecptionListener execptionListener;

    private static final Logger logger =
            LoggerFactory.getLogger(CSVParser.class);

    public CSVParser() {}

    public void registerListener(ExecptionListener listener) {
        this.execptionListener = listener;
    }

    public Stream<CSVUserDTO> parse(Reader reader) {

        if (reader == null) {
            logger.warn("Input reader is null - return empty stream");

            return Stream.empty();
        }

        CsvToBean<CSVUserDTO> beans = new CsvToBeanBuilder<CSVUserDTO>(reader)
                .withType(CSVUserDTO.class)
                .withIgnoreEmptyLine(true)
                .withExceptionHandler(this::handleException)
                .build();

        logger.debug("Build CsvToBean instance for CSVUserDTO");

        return this.prepareStream(beans::stream);
    }

    private CsvException handleException(CsvException e) {
        logger.warn("Received CsvException while parsing CSV file");

        Optional.ofNullable(this.execptionListener).ifPresent(listener -> listener.notify(e));

        return e;
    }

    private Stream<CSVUserDTO> prepareStream(Supplier<Stream<CSVUserDTO>> csvStream) {
        try {
            return csvStream.get();
        } catch (RuntimeException e) {
            logger.warn("Received RuntimeException while getting CSV stream");

            Throwable cause = e.getCause();

            if (!(cause instanceof CsvException)) {
                // Rethrow exception as it is not expected.
                throw e;
            }

            this.handleException((CsvException) cause);

            return Stream.empty();
        }
    }

}
