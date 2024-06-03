package de.triology.universeadm.mail;

import de.triology.universeadm.configuration.MailConfiguration;
import de.triology.universeadm.user.User;
import jakarta.mail.*;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MailServiceImplTest {

    private final User userMock = mock(User.class);
    private final MailConfiguration mailConfigurationMock = mock(MailConfiguration.class);
    private final SessionFactory sessionFactoryMock = mock(SessionFactory.class);
    private final MailSender mailSenderMock = mock(MailSender.class);

    @Test
    public void testNotify() {
        String template = "This is a test for ${username} with ${password}";

        when(mailConfigurationMock.getFrom()).thenReturn("no-reply@test.com");
        when(mailConfigurationMock.getSubject()).thenReturn("TestSubject");
        when(mailConfigurationMock.getMessage()).thenReturn(template);
        when(sessionFactoryMock.createSession()).thenReturn(Session.getInstance(new Properties()));
        when(userMock.getUsername()).thenReturn("testUser");
        when(userMock.getPassword()).thenReturn("testPassword");
        when(userMock.getMail()).thenReturn("testUser@test.com");

        MailService mailService = new MailServiceImpl(mailConfigurationMock, sessionFactoryMock, mailSenderMock);
        mailService.notify(userMock);

        ArgumentMatcher<Message> matcher = message -> {

                try {
                    String msgContent = message.getContent().toString();

                    assertTrue(msgContent.contains("testUser"));
                    assertTrue(msgContent.contains("testPassword"));

                    assertEquals("TestSubject", message.getSubject());

                    Address[] sender = message.getFrom();
                    assertEquals(1, sender.length);
                    assertEquals("no-reply@test.com", sender[0].toString());

                    Address[] recipient = message.getAllRecipients();
                    assertEquals(1, recipient.length);
                    assertEquals("testUser@test.com", recipient[0].toString());
                } catch (Exception e) {
                    fail(e.getMessage());
                }

                return true;
        };

        verify(mailSenderMock, times(1)).sendAsync(argThat(matcher));
    }

    @Test
    public void testNotifyWrongTemplate() {
        String template = "This is a test for username with ${password}";

        when(mailConfigurationMock.getFrom()).thenReturn("no-reply@test.com");
        when(mailConfigurationMock.getSubject()).thenReturn("TestSubject");
        when(mailConfigurationMock.getMessage()).thenReturn(template);
        when(sessionFactoryMock.createSession()).thenReturn(Session.getInstance(new Properties()));
        when(userMock.getUsername()).thenReturn("testUser");
        when(userMock.getPassword()).thenReturn("testPassword");
        when(userMock.getMail()).thenReturn("testUser@test.com");

        MailService mailService = new MailServiceImpl(mailConfigurationMock, sessionFactoryMock, mailSenderMock);
        mailService.notify(userMock);

        ArgumentMatcher<Message> matcher = message -> {

            try {
                String msgContent = message.getContent().toString();

                assertFalse(msgContent.contains("testUser"));
                assertTrue(msgContent.contains("testPassword"));

                assertEquals("TestSubject", message.getSubject());

                Address[] sender = message.getFrom();
                assertEquals(1, sender.length);
                assertEquals("no-reply@test.com", sender[0].toString());

                Address[] recipient = message.getAllRecipients();
                assertEquals(1, recipient.length);
                assertEquals("testUser@test.com", recipient[0].toString());
            } catch (Exception e) {
                fail(e.getMessage());
            }

            return true;
        };

        verify(mailSenderMock, times(1)).sendAsync(argThat(matcher));
    }

    @Test
    public void testNotifyEmptyMessage() {
        String template = "This is a test for username with ${password}";

        when(mailConfigurationMock.getFrom()).thenReturn("no-reply@test.com");
        when(mailConfigurationMock.getSubject()).thenReturn("");
        when(mailConfigurationMock.getMessage()).thenReturn(template);
        when(sessionFactoryMock.createSession()).thenReturn(Session.getInstance(new Properties()));
        when(userMock.getUsername()).thenReturn("testUser");
        when(userMock.getPassword()).thenReturn("testPassword");
        when(userMock.getMail()).thenReturn("testUser@test.com");

        MailService mailService = new MailServiceImpl(mailConfigurationMock, sessionFactoryMock, mailSenderMock);
        mailService.notify(userMock);

        verify(mailSenderMock, never()).sendAsync(any());
    }
}
