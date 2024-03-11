package de.triology.universeadm.mail;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Module.class)
public class MailModule extends AbstractModule {

    @Override
    protected void configure()
    {
        bind(MailService.class).to(MailServiceImpl.class);
        bind(SessionFactory.class).asEagerSingleton();
        bind(MailSender.class).asEagerSingleton();
    }
}
