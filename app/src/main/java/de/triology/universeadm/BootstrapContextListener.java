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

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import de.triology.universeadm.configuration.ApplicationConfiguration;
import de.triology.universeadm.configuration.Language;
import de.triology.universeadm.configuration.I18nConfiguration;
import de.triology.universeadm.configuration.MailConfiguration;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class BootstrapContextListener
        extends GuiceResteasyBootstrapServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapContextListener.class);

    /**
     * Method description
     *
     * @param context
     * @return
     */
    @Override
    protected List<? extends Module> getModules(ServletContext context) {
        LDAPConfiguration ldapConfiguration = BaseDirectory.getConfiguration("ldap.xml", LDAPConfiguration.class);
        MailConfiguration mailConfiguration = BaseDirectory.getConfiguration("mail.xml", MailConfiguration.class);
        I18nConfiguration i18nConfiguration = new I18nConfiguration(Language.en, Language.de);

        List<? extends Module> modules;

        if (ldapConfiguration.isDisabled()) {
            logger.warn("ldap is disable load error module");
            modules = ImmutableList.of(new LDAPDisabledModule());
        } else {
            logger.info("load injection modules");

            Module securityModule;
            if (Stage.get() == Stage.PRODUCTION) {
                logger.info("load cas security module for production stage");
                securityModule = new CasSecurityModule(context);
            } else {
                logger.info("load development security module for development stage");
                securityModule = new DevelopmentSecurityModule(context);
            }

            //J-
            modules = ImmutableList.of(
                    ShiroWebModule.guiceFilterModule(),
                    new MainModule(ldapConfiguration, mailConfiguration, i18nConfiguration),
                    securityModule
            );
            //J+
        }

        return modules;
    }
}
