package de.triology.universeadm.validation;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.kohsuke.MetaInfServices;

//~--- JDK imports ------------------------------------------------------------

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@MetaInfServices(Module.class)
public class ValidationModule extends AbstractModule
{

  /**
   * Method description
   *
   */
  @Override
  protected void configure()
  {
    bind(ValidatorFactory.class).toInstance(
      Validation.buildDefaultValidatorFactory());
    bind(Validator.class).to(HibernateValidator.class);
    bind(HibernateValidatorExceptionMapping.class);
  }
}
