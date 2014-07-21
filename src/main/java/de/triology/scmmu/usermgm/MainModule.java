/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.triology.scmmu.usermgm.user.LDAPUserManager;
import de.triology.scmmu.usermgm.user.UserManager;
import de.triology.scmmu.usermgm.user.UserResource;

/**
 *
 * @author Sebastian Sdorra
 */
public class MainModule extends AbstractModule
{
  
  /**
   * the logger for MainModule
   */
  private static final Logger logger = LoggerFactory.getLogger(MainModule.class);

  @Override
  protected void configure()
  {
    logger.info("bind resources");
    
    //J-
    bind(LDAPConfiguration.class).toInstance(
      BaseDirectory.getConfiguration("ldap.xml", LDAPConfiguration.class)
    );
    //J+
    bind(LDAPHasher.class).toInstance(new LDAPHasher());
    bind(EventBus.class).toInstance(new EventBus());
    bind(LDAPConnectionStrategy.class).to(DefaultLDAPConnectionStrategy.class);
    bind(UserManager.class).to(LDAPUserManager.class);
    bind(UserResource.class);
  }
  
}
