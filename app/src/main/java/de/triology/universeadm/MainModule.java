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
