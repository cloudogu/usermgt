package de.triology.universeadm.mail;

import com.google.inject.Inject;
import de.triology.universeadm.configuration.MailConfiguration;
import de.triology.universeadm.user.User;
import jakarta.mail.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MailServiceImpl implements MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private final MailConfiguration mailConfiguration;
    private final Session session;
    private final MailSender mailSender;

    @Inject
    public MailServiceImpl(MailConfiguration mailConfiguration, SessionFactory sessionProviderFactory, MailSender mailSender){
        this.mailConfiguration = mailConfiguration;
        this.session = sessionProviderFactory.createSession();
        this.mailSender = mailSender;
    }

    @Override
    public void notify(User user) {
        MessageBuilder builder = new MessageBuilder(session);

        builder
            .from(mailConfiguration.getFrom())
            .to(user.getMail())
            .subject(mailConfiguration.getSubject())
            .content(getMailContent(user))
            .build()
            .ifPresent((msg -> {
                logger.debug("Built message for user {}", user.getUsername());
                this.mailSender.sendAsync(msg);
            }));
    }

    @Override
    public void notifyRobotRenewal(String recipient, String robotUsername, String expiresAt) {
        String content = "The access token for the robot account \"" + robotUsername + "\" was renewed.";
        if (expiresAt != null && !expiresAt.trim().isEmpty()) {
            content += "\n\nThe new token expires at " + expiresAt + ".";
        }

        new MessageBuilder(session)
            .from(mailConfiguration.getFrom())
            .to(recipient)
            .subject("Robot account access token renewed")
            .content(content)
            .build()
            .ifPresent(message -> mailSender.sendAsync(message).whenComplete((result, error) -> {
                if (error != null) {
                    logger.warn("Could not send renewal notification for robot account {}", robotUsername, error);
                }
            }));
    }

    private String getMailContent(User user) {
        Map<String, String> substitutes = new HashMap<>();

        substitutes.put(USER_NAME, user.getUsername());
        substitutes.put(PASSWORD, user.getPassword());

        String template = mailConfiguration.getMessage();
        StringSubstitutor sub = new StringSubstitutor(substitutes);
        return sub.replace(template);
    }
}
