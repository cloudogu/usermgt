package de.triology.universeadm.user.imports;

import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.lang.reflect.Field;

public class CustomCsvDataTypeMismatchException extends CsvDataTypeMismatchException {

    private final Field affectedField;

    public CustomCsvDataTypeMismatchException(Field affectedField, CsvDataTypeMismatchException origin) {
        super(origin.getSourceObject(), origin.getDestinationClass(), origin.getMessage());
        this.affectedField = affectedField;
    }

    public Field getAffectedField() {
        return affectedField;
    }
}
