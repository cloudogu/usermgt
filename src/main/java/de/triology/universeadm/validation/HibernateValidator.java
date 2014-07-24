/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.validation;

import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

/**
 *
 * @author ssdorra
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
