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

//~--- non-JDK imports --------------------------------------------------------

import com.github.legman.EventBus;
import com.github.legman.guice.LegmanModule;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import de.triology.universeadm.configuration.ApplicationConfiguration;
import de.triology.universeadm.configuration.I18nConfiguration;
import de.triology.universeadm.configuration.MailConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class MainModule extends ServletModule {

    /**
     * the logger for MainModule
     */
    private static final Logger logger =
            LoggerFactory.getLogger(MainModule.class);

    //~--- constructors ---------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param ldapConfiguration
     */
    public MainModule(ApplicationConfiguration applicationConfiguration, LDAPConfiguration ldapConfiguration, MailConfiguration mailConfiguration, I18nConfiguration i18nConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.ldapConfiguration = ldapConfiguration;
        this.mailConfiguration = mailConfiguration;
        this.i18nConfiguration = i18nConfiguration;
    }

    //~--- methods --------------------------------------------------------------

    /**
     * Method description
     */
    @Override
    protected void configureServlets() {
        logger.info("bind resources");

        bind(ApplicationConfiguration.class).toInstance(applicationConfiguration);
        bind(LDAPConfiguration.class).toInstance(ldapConfiguration);
        bind(MailConfiguration.class).toInstance(mailConfiguration);
        bind(I18nConfiguration.class).toInstance(i18nConfiguration);

        // events
        EventBus eventBus = new EventBus();

        install(new LegmanModule(eventBus));
        bind(EventBus.class).toInstance(eventBus);

        // ldap stuff
        bind(LDAPHasher.class).toInstance(new LDAPHasher());
        bind(LDAPConnectionStrategy.class).to(DefaultLDAPConnectionStrategy.class);

        // install package modules
        logger.info("load modules from classpath and install them");

        for (Module module : ServiceLoader.load(Module.class)) {
            logger.info("install module {}", module.getClass());
            install(module);
        }

        // other jax-rs stuff
        bind(DisableCacheResponseFilter.class);
        bind(CatchAllExceptionMapper.class);
        bind(SubjectResource.class);
        bind(LogoutResource.class);

        // filter
        filter("/*").through(LDAPConnectionStrategyBindFilter.class);

        // serve resources
        serve("/components/*", "/scripts/*",
                "/style/*").with(ResourceServlet.class);

        // serve index page for everything but "/api" or "/assets"
        serveRegex("^/(?!api|assets).+").with(TemplateServlet.class);
    }

    //~--- fields ---------------------------------------------------------------

    /**
     * Field description
     */
    private final ApplicationConfiguration applicationConfiguration;
    private final LDAPConfiguration ldapConfiguration;
    private final MailConfiguration mailConfiguration;
    private final I18nConfiguration i18nConfiguration;
}
