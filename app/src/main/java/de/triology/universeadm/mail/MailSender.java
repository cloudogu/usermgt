package de.triology.universeadm.mail;


import com.google.inject.Inject;
import de.triology.universeadm.configreader.ApplicationConfigReader;
import de.triology.universeadm.configreader.ApplicationConfiguration;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailSender {
    private static final String MAIL_ADDRESS_REGEX = "(.*@.*\\..*)";
    private static final String ILLEGAL_MAIL_ADDRESS_EXCEPTION_MESSAGE = "Receiver is not a valid Mail Address";
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html; charset=utf-8";
    private static final String NO_ADDRESS = "noAddress";
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";

    private final MessageBuilder messageBuilder;
    private final TransportSender sender;

    private final ApplicationConfiguration applicationConfig;

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

    @Inject
    public MailSender(ApplicationConfiguration applicationConfig ) {
        this(new MessageBuilder(), new TransportSender(), applicationConfig);
    }

    public MailSender(MessageBuilder messageBuilder, TransportSender sender, ApplicationConfiguration applicationConfig) {
        this.messageBuilder = messageBuilder;
        this.sender = sender;
        this.applicationConfig = applicationConfig;
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
        final String input = applicationConfig.getSenderMail();
        if (input == null || input.equals("")) {
            return new InternetAddress(NO_ADDRESS);
        }
        return new InternetAddress(input);
    }

    private Properties createProperties() {
        Properties prop = new Properties();
        prop.put(MAIL_SMTP_HOST, applicationConfig.getHost());
        prop.put(MAIL_SMTP_PORT,
                applicationConfig.getPort());
        return prop;
    }
}
