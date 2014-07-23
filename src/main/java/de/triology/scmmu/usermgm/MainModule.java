/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.scmmu.usermgm;

import de.triology.scmmu.usermgm.validation.Validator;
import de.triology.scmmu.usermgm.validation.HibernateValidator;
import com.google.common.eventbus.EventBus;
import com.google.inject.servlet.ServletModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.triology.scmmu.usermgm.user.LDAPUserManager;
import de.triology.scmmu.usermgm.user.UserManager;
import de.triology.scmmu.usermgm.user.UserResource;
import de.triology.scmmu.usermgm.validation.HibernateValidatorExceptionMapping;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Sebastian Sdorra
 */
public class MainModule extends ServletModule
{

  /**
   * the logger for MainModule
   */
  private static final Logger logger = LoggerFactory.getLogger(MainModule.class);

  @Override
  protected void configureServlets()
  {
    logger.info("bind resources");

    bind(LDAPConfiguration.class).toInstance(
      BaseDirectory.getConfiguration("ldap.xml", LDAPConfiguration.class)
    );

    // validation
    bind(ValidatorFactory.class).toInstance(Validation.buildDefaultValidatorFactory());
    bind(Validator.class).to(HibernateValidator.class);
    bind(HibernateValidatorExceptionMapping.class);

    // misc
    bind(LDAPHasher.class).toInstance(new LDAPHasher());
    bind(EventBus.class).toInstance(new EventBus());
    bind(LDAPConnectionStrategy.class).to(DefaultLDAPConnectionStrategy.class);
    bind(UserManager.class).to(LDAPUserManager.class);
    bind(UserResource.class);
    bind(SubjectResource.class);

    // serve index pages
    serve("/", "/index.html", "/index-debug.html").with(TemplateServlet.class);
  }

}
