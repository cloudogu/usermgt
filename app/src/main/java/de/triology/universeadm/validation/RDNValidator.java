package de.triology.universeadm.validation;

import com.google.common.base.Strings;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class RDNValidator implements ConstraintValidator<RDN, String> {
    public static final String ERROR_TOO_SHORT = "ERROR_LENGTH_TOO_SMALL";
    public static final String ERROR_TOO_LONG = "ERROR_LENGTH_TOO_HIGH";
    public static final String ERROR_INVALID_CHARACTERS = "ERROR_INVALID_CHARACTERS";

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9-_@\\.]{2,128}");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (value.length() < 2) {
            context.buildConstraintViolationWithTemplate(ERROR_TOO_SHORT)
                .addConstraintViolation();
            return false;
        } else if (value.length() > 128){
            context.buildConstraintViolationWithTemplate(ERROR_TOO_LONG)
                .addConstraintViolation();
            return false;
        } else if (!PATTERN.matcher(Strings.nullToEmpty(value)).matches()){
            context.buildConstraintViolationWithTemplate(ERROR_INVALID_CHARACTERS)
                .addConstraintViolation();
            return false;
        }

        return true;
    }

}
