package de.triology.universeadm.mail;

import de.triology.universeadm.configuration.ApplicationConfiguration;
import de.triology.universeadm.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class MailSenderTest {
    public static final String TEST = "test";
    private static final String MAIL_CONTENT = "Willkommen zum Cloudogu Ecosystem!\n" +
            "Dies ist ihr Benutzeraccount\n" +
            "Benutzername = %s\n" +
            "Passwort = %s\n" +
            "Bei der ersten Anmeldung müssen sie ihr Passwort ändern\n";

    private MailSender.MessageBuilder messageBuilder;
    private MailSender.TransportSender transportSender;
    private Message message;
    private MailSender mailSender;
    private ApplicationConfiguration applicationConfig;
    private final User user = new User(
            "Tester",
            "Tester",
            "Tes",
            "Ter",
            "test@test.com",
            "temp",
            true,
            new ArrayList<String>());

    @Before
    public void setUp() {
        this.message = mock(Message.class);
        this.messageBuilder = mock(MailSender.MessageBuilder.class);
        this.transportSender = mock(MailSender.TransportSender.class);
        this.applicationConfig = mock(ApplicationConfiguration.class);
        this.mailSender = new MailSender(this.messageBuilder, this.transportSender, this.applicationConfig);
        when(this.messageBuilder.build(Matchers.<Session>any())).thenReturn(this.message);
        when(applicationConfig.getHost()).thenReturn("postifx");
        when(applicationConfig.getPort()).thenReturn("25");
    }

    @Test
    public void sendMailSuccessful() throws MessagingException, IOException {

        String content = String.format(MAIL_CONTENT, user.getUsername(), TEST);
        this.mailSender.sendMail(TEST, content, user.getMail());
        ArgumentCaptor<Multipart> argument = ArgumentCaptor.forClass(Multipart.class);
        verify(message).setContent(argument.capture());
        String actualMsgBodyContent = argument.getValue().getBodyPart(0).getContent().toString();
        Assert.assertEquals(content, actualMsgBodyContent);
        verify(this.transportSender, times(1)).send(Matchers.<Message>any());
    }

    @Test(expected = NullPointerException.class)
    public void nullValueInMethodCall() throws MessagingException {
        this.mailSender.sendMail(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void InvalidMailMethodCall() throws MessagingException {
        String content = String.format(MAIL_CONTENT, user.getUsername(), TEST);
        this.mailSender.sendMail(TEST, content, TEST);
    }
}
