package de.triology.universeadm.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.configuration.MailConfiguration;
import jakarta.mail.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

@Singleton
public class SessionFactory {
    private static final Logger logger = LoggerFactory.getLogger(SessionFactory.class);
    private final MailConfiguration mailConfiguration;
    private Session session;
    @Inject
    public SessionFactory(MailConfiguration mailConfiguration){
        this.mailConfiguration = mailConfiguration;
    }

    public Session createSession() {
        if (Optional.ofNullable(session).isPresent()){
            return session;
        }

        String host = mailConfiguration.getHost();
        String port = mailConfiguration.getPort();

        logger.debug("Try to create smtp session for host {} on port {}", host, port);

        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailConfiguration.getHost());
        properties.put("mail.smtp.port", mailConfiguration.getPort());


        session = Session.getInstance(properties, null);

        return session;
    }

}
