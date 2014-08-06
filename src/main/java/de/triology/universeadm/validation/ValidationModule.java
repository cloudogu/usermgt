/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.validation;

import com.google.inject.AbstractModule;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 *
 * @author ssdorra
 */
public class ValidationModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(ValidatorFactory.class).toInstance(Validation.buildDefaultValidatorFactory());
    bind(Validator.class).to(HibernateValidator.class);
    bind(HibernateValidatorExceptionMapping.class);
  }

}
