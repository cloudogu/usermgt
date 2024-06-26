package de.triology.universeadm.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MessageBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MessageBuilder.class);
    private final Session session;

    private String from = "";
    private String to = "";
    private String subject = "";
    private String content = "";

    public MessageBuilder(Session session) {
        this.session = session;
    }

    public MessageBuilder from(String from) {
        this.from = from;
        return this;
    }

    public MessageBuilder to(String to) {
        this.to = to;
        return this;
    }

    public MessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageBuilder content(String content) {
        this.content = content;
        return this;
    }

    public Optional<Message> build() {
        if(!validate()){
            return Optional.empty();
        }

        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);
        } catch (MessagingException e) {
            logger.error("Unable to build message", e);
            return Optional.empty();
        }

        return Optional.of(message);
    }

    private boolean validate() {
        if(from.isEmpty()) {
            logger.error("Sender is empty in mail");
            return false;
        }

        if(to.isEmpty()) {
            logger.error("Recipient is empty in mail");
            return false;
        }

        if(subject.isEmpty()) {
            logger.error("Subject is empty in mail");
            return false;
        }

        if(content.isEmpty()) {
            logger.error("Mail content is empty");
            return false;
        }

        return true;
    }
}
