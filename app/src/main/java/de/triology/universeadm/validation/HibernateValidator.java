package de.triology.universeadm.validation;

import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class HibernateValidator implements Validator
{
  private final ValidatorFactory validatorFactory;

  @Inject
  public HibernateValidator(ValidatorFactory validatorFactory)
  {
    this.validatorFactory = validatorFactory;
  }
  
  @Override
  public <T> void validate(T object, String msg){
    Set<ConstraintViolation<T>> violations = validatorFactory.getValidator().validate(object);
    if (!violations.isEmpty()){
      throw new ConstraintViolationException(msg, new HashSet<ConstraintViolation<?>>(violations));
    }
  }
}
