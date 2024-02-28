package de.triology.universeadm.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.*;

public class MessageBuilder {

    private final Session session;
    private String from;
    private String to;
    private String subject;
    private Multipart content;

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

    public MessageBuilder content(String content) throws MessagingException {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            this.content = multipart;

            return this;
    }

    public Message build() throws MessagingException {
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(content);

        return message;
    }
}
