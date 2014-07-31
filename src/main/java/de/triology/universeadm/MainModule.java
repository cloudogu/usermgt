/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import de.triology.universeadm.validation.Validator;
import de.triology.universeadm.validation.HibernateValidator;
import com.google.common.eventbus.EventBus;
import com.google.inject.servlet.ServletModule;
import de.triology.universeadm.account.AccountManager;
import de.triology.universeadm.account.AccountResource;
import de.triology.universeadm.account.DefaultAccountManager;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.group.GroupResource;
import de.triology.universeadm.group.LDAPGroupManager;
import de.triology.universeadm.mapping.DefaultMapperFactory;
import de.triology.universeadm.mapping.InjectorMappingConverterFactory;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.MappingConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.triology.universeadm.user.LDAPUserManager;
import de.triology.universeadm.user.UserManager;
import de.triology.universeadm.user.UserResource;
import de.triology.universeadm.validation.HibernateValidatorExceptionMapping;
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

    // events
    bind(EventBus.class).toInstance(new EventBus());
    
    // ldap stuff
    bind(LDAPHasher.class).toInstance(new LDAPHasher());
    bind(LDAPConnectionStrategy.class).to(DefaultLDAPConnectionStrategy.class);
    
    // mapping
    bind(MappingConverterFactory.class).to(InjectorMappingConverterFactory.class);
    bind(MapperFactory.class).to(DefaultMapperFactory.class);
    
    // accont
    bind(AccountManager.class).to(DefaultAccountManager.class);
    bind(AccountResource.class);
    
    // users
    bind(UserManager.class).to(LDAPUserManager.class);
    bind(UserResource.class);
    
    // groups
    bind(GroupManager.class).to(LDAPGroupManager.class);
    bind(GroupResource.class);
    
    // other resources
    bind(SubjectResource.class);
    bind(LogoutResource.class);

    // filter
    filter("/*").through(LDAPConnectionStrategyBindFilter.class);
    
    // serve index pages
    serve("/", "/index.html", "/index-debug.html").with(TemplateServlet.class);
  }

}
