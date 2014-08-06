/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.github.legman.EventBus;
import com.github.legman.guice.LegmanModule;
import com.google.inject.servlet.ServletModule;
import de.triology.universeadm.account.AccountModule;
import de.triology.universeadm.group.GroupModule;
import de.triology.universeadm.mapping.MappingModule;
import de.triology.universeadm.settings.SettingsModule;
import de.triology.universeadm.user.UserModule;
import de.triology.universeadm.validation.ValidationModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    install(new ValidationModule());

    // events
    EventBus eventBus = new EventBus();
    install(new LegmanModule(eventBus));
    bind(EventBus.class).toInstance(eventBus);

    // ldap stuff
    bind(LDAPHasher.class).toInstance(new LDAPHasher());
    bind(LDAPConnectionStrategy.class).to(DefaultLDAPConnectionStrategy.class);

    // mapping
    install(new MappingModule());

    // accont
    install(new AccountModule());

    // users
    install(new UserModule());

    // groups
    install(new GroupModule());
    
    // settings
    install(new SettingsModule());

    // other jax-rs stuff
    bind(CatchAllExceptionMapper.class);
    bind(SubjectResource.class);
    bind(LogoutResource.class);

    // filter
    filter("/*").through(LDAPConnectionStrategyBindFilter.class);

    // serve index pages
    serve("/", "/index.html", "/index-debug.html").with(TemplateServlet.class);
  }

}
