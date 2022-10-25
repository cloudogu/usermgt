package de.triology.universeadm.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailSender {
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html; charset=utf-8";
    private static final String NOADDRESS = "noaddress";
    private static final String MAIL_ADDRESS = "MAIL_ADDRESS";
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String PORT = "25";
    private static final String HOST = "postfix";
    private static final String MAIL_ADDRESS_REGEX = "(.*@.*\\..*)";
    private static final String ILLEGAL_MAIL_ADDRESS_EXCEPTION_MESSAGE = "Receiver ist keine valide Mail Addresse";
    private final MessageBuilder messageBuilder;
    private final TransportSender sender;

    public static class MessageBuilder {
        public Message build(Session session) {
            return new MimeMessage(session);
        }
    }

    public static class TransportSender {
        public void send(Message msg) throws MessagingException {
            Transport.send(msg);
        }
    }

    public MailSender() {
        this(new MessageBuilder(), new TransportSender());
    }

    public MailSender(MessageBuilder messageBuilder, TransportSender sender) {
        this.messageBuilder = messageBuilder;
        this.sender = sender;
    }

    public void sendMail(String subject, String content, String receiver) throws MessagingException {
        if (subject == null || content == null || subject.equals("") || content.equals("")) {
            throw new NullPointerException();
        }
        if(!receiver.matches(MAIL_ADDRESS_REGEX)){
            throw new IllegalArgumentException(ILLEGAL_MAIL_ADDRESS_EXCEPTION_MESSAGE);
        }

        final Properties prop = createProperties();
        Session session = Session.getInstance(prop);

        final Message message = this.messageBuilder.build(session);

        message.setFrom(getInternetAddress());
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(receiver));
        message.setSubject(subject);

        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, TEXT_HTML_CHARSET_UTF_8);

        final Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        this.sender.send(message);
    }

    private InternetAddress getInternetAddress() throws AddressException {
        final String input = System.getenv(MAIL_ADDRESS);
        if (input == null || input.equals("")) {
            return new InternetAddress(NOADDRESS);
        }
        return new InternetAddress(input);
    }

    private Properties createProperties() {
        Properties prop = new Properties();
        prop.put(MAIL_SMTP_HOST, HOST);
        prop.put(MAIL_SMTP_PORT,
                PORT);
        return prop;
    }
}
