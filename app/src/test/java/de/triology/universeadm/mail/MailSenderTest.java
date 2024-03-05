package de.triology.universeadm.mail;

import de.triology.universeadm.configuration.MailConfiguration;
import jakarta.mail.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MailSenderTest {

    private Transport transportMock;
    private Message messageMock;
    private MailSender mailSender;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactoryMock = mock(SessionFactory.class);
        Session sessionMock = mock(Session.class);
        transportMock = mock(Transport.class);
        messageMock = mock(Message.class);

        when(sessionFactoryMock.createSession()).thenReturn(sessionMock);
        when(sessionMock.getTransport()).thenReturn(transportMock);

        MailConfiguration mailConfiguration = new MailConfiguration("", "", "", "", "", 3, 10, 1);
        mailSender = new MailSender(sessionFactoryMock, mailConfiguration);
    }

    @Test
    public void sendInvalidRecipient() throws MessagingException {
        when(messageMock.getSubject()).thenReturn("test");
        when(messageMock.getAllRecipients()).thenThrow(new MessagingException("Invalid recipient"));

        this.mailSender.send(messageMock);

        verify(transportMock, never()).isConnected();
        verify(transportMock, never()).sendMessage(any(), any());

        assertEquals(0, mailSender.getRetryMessageCount());
    }

    @Test
    public void sendInvalidSubject() throws MessagingException {
        when(messageMock.getSubject()).thenThrow(new MessagingException("Invalid subject"));
        when(messageMock.getAllRecipients()).thenReturn(new Address[]{});

        this.mailSender.send(messageMock);

        verify(transportMock, never()).isConnected();
        verify(transportMock, never()).sendMessage(any(), any());

        assertEquals(0, mailSender.getRetryMessageCount());
    }

    @Test
    public void sendWhenConnected() throws MessagingException, InterruptedException {
        when(messageMock.getSubject()).thenReturn("test");
        when(messageMock.getAllRecipients()).thenReturn(new Address[]{});
        when(transportMock.isConnected()).thenReturn(true);

        this.mailSender.send(messageMock);

        // I know it's bad
        Thread.sleep(500);

        verify(transportMock, times(1)).isConnected();
        verify(transportMock, never()).connect();
        verify(transportMock, times(1)).sendMessage(any(), any());

        assertEquals(0, mailSender.getRetryMessageCount());
    }

    @Test
    public void sendWithReconnect() throws MessagingException {
        when(messageMock.getSubject()).thenReturn("test");
        when(messageMock.getAllRecipients()).thenReturn(new Address[]{});
        when(transportMock.isConnected()).thenReturn(false);

        this.mailSender.send(messageMock);

        verify(transportMock, times(1)).isConnected();
        verify(transportMock, times(1)).connect();
        verify(transportMock, times(1)).sendMessage(any(), any());

        assertEquals(0, mailSender.getRetryMessageCount());
    }

    @Test
    public void sendDisconnected() throws MessagingException {
        when(messageMock.getSubject()).thenReturn("test");
        when(messageMock.getAllRecipients()).thenReturn(new Address[]{});
        when(transportMock.isConnected()).thenReturn(false);
        doThrow(new MessagingException("no connection")).when(transportMock).connect();

        this.mailSender.send(messageMock);

        verify(transportMock, times(1)).isConnected();
        verify(transportMock, times(1)).connect();
        verify(transportMock, never()).sendMessage(any(), any());

        assertEquals(1, mailSender.getRetryMessageCount());

    }

    @Test
    public void sendFailed() throws MessagingException, InterruptedException {
        when(messageMock.getSubject()).thenReturn("test");
        when(messageMock.getAllRecipients()).thenReturn(new Address[]{});
        when(transportMock.isConnected()).thenReturn(true);
        doThrow(new MessagingException("Failed to send")).when(transportMock).sendMessage(any(), any());

        this.mailSender.send(messageMock);

        // I know it's bad
        Thread.sleep(500);

        assertEquals(1, mailSender.getRetryMessageCount());
    }
}
