package de.triology.universeadm.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

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

    public MessageBuilder content(String content) throws MessageBuilderException {
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            this.content = multipart;

            return this;
        } catch (MessagingException e) {
            throw new MessageBuilderException("Unable set content for mail", e);
        }
    }

    public Message build() throws MessageBuilderException {
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(content);
        } catch (MessagingException e) {
            throw new MessageBuilderException("Unable to build message", e);
        }

        return message;
    }


}
