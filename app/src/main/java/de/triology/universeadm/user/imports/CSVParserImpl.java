package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
                        .map(e -> ImportEntryResult.skipped(mapCSVExceptionToImportError(e))))
                .orElse(Stream.empty());
    }

    /**
     * Prepares the stream by calling the supplier function for the csv stream. Doing this can cause a
     * CsvException wrapped inside a RuntimeException when the header row of the csv cannot be parsed.
     *
     * @param csvStream - supplier to get the stream
     * @return Stream containing {@link CSVUserDTO} elements
     * @throws MissingHeaderFieldException - wraps the CsvException
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

    private ImportError mapCSVExceptionToImportError(CsvException e) {
        Function<ImportError.Code, ImportError.Builder> createImportBuilder = code ->
                new ImportError.Builder(code)
                        .withLineNumber(e.getLineNumber())
                        .withErrorMessage(e.getMessage());

        if (e instanceof CsvDataTypeMismatchException) {
            ImportError.Builder builder = createImportBuilder.apply(ImportError.Code.FIELD_CONVERSION_ERROR);

            if (e instanceof CustomCsvDataTypeMismatchException) {
                String affectedColumn = ((CustomCsvDataTypeMismatchException) e).getAffectedField().getName();
                builder.withAffectedColumns(Collections.singletonList((affectedColumn)));
            }

            return builder.build();
        }

        if (e instanceof CsvRequiredFieldEmptyException) {
            CsvRequiredFieldEmptyException exception = (CsvRequiredFieldEmptyException) e;

            List<String> columns = exception.getDestinationFields().stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());

            return createImportBuilder
                    .apply(ImportError.Code.MISSING_FIELD_ERROR)
                    .withAffectedColumns(columns)
                    .build();
        }

        if (e instanceof CsvFieldAssignmentException) {
            return createImportBuilder
                    .apply(ImportError.Code.FIELD_ASSIGNMENT_ERROR)
                    .build();
        }

        return createImportBuilder
                .apply(ImportError.Code.PARSING_ERROR)
                .build();
    }

}
