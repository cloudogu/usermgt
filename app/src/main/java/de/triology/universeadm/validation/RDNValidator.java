package de.triology.universeadm.validation;

import com.google.common.base.Strings;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class RDNValidator implements ConstraintValidator<RDN, String>
{

  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9-_@\\.]{2,128}");
  
  @Override
  public void initialize(RDN rdn)
  {

  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context)
  {
    return PATTERN.matcher(Strings.nullToEmpty(value)).matches();
  }

}
