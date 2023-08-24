package de.triology.universeadm.user.imports;

import com.opencsv.bean.customconverter.ConverterLanguageToBoolean;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

/**
 * CustomConverterBoolean extends the ConverterLanguageToBoolean by wrapping the
 * CsvDataTypeMismatchException into CustomCsvDataTypeMismatchException. It uses the default
 * {@link BooleanConverter} which means only English language is supported for parsing boolean values.
 */
public class CustomConverterBoolean<T, I> extends ConverterLanguageToBoolean<T, I> {
    @Override
    protected String getLocalizedTrue() {
        return null;
    }

    @Override
    protected String getLocalizedFalse() {
        return null;
    }

    @Override
    protected String[] getAllLocalizedTrueValues() {
        return new String[0];
    }

    @Override
    protected String[] getAllLocalizedFalseValues() {
        return new String[0];
    }

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            Converter bc = new BooleanConverter();

            try {
                return bc.convert(Boolean.class, value.trim());
            } catch (ConversionException var5) {
                CsvDataTypeMismatchException csve = new CsvDataTypeMismatchException(value, this.field.getType(), ResourceBundle.getBundle("convertLanguageToBoolean", this.errorLocale).getString("input.not.boolean"));
                csve.initCause(var5);

                throw new CustomCsvDataTypeMismatchException(this.field, csve);
            }
        }
    }
}
