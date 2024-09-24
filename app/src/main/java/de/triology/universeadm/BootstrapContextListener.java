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
        ApplicationConfiguration applicationConfiguration = BaseDirectory.getConfiguration("application-configuration.xml", ApplicationConfiguration.class);
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
                    new MainModule(applicationConfiguration, ldapConfiguration, mailConfiguration, i18nConfiguration),
                    securityModule
            );
            //J+
        }

        return modules;
    }
}
