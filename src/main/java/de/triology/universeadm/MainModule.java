/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */

package de.triology.universeadm;

import com.github.legman.EventBus;
import com.github.legman.guice.LegmanModule;
import com.google.inject.servlet.ServletModule;
import de.triology.universeadm.account.AccountModule;
import de.triology.universeadm.backup.BackupModule;
import de.triology.universeadm.group.GroupModule;
import de.triology.universeadm.mapping.MappingModule;
import de.triology.universeadm.settings.SettingsModule;
import de.triology.universeadm.user.UserModule;
import de.triology.universeadm.validation.ValidationModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
    
    // backup
    install(new BackupModule());
    
    // settings
    install(new SettingsModule());

    // other jax-rs stuff
    bind(CatchAllExceptionMapper.class);
    bind(SubjectResource.class);
    bind(LogoutResource.class);

    // filter
    filter("/*").through(LDAPConnectionStrategyBindFilter.class);

    // serve resources
    serve("/components/*", "/scripts/*", "/style/*").with(ResourceServlet.class);
    // serve index pages
    serve("/", "/index.html", "/index-debug.html").with(TemplateServlet.class);
  }

}
