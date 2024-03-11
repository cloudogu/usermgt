package de.triology.universeadm.mail;

import de.triology.universeadm.configuration.MailConfiguration;
import jakarta.mail.Session;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionFactoryTest {

    private final MailConfiguration mailConfigurationMock = mock(MailConfiguration.class);

    @Test
    public void createSession() {
        when(mailConfigurationMock.getHost()).thenReturn("testHost");
        when(mailConfigurationMock.getPort()).thenReturn("25");

        SessionFactory sessionFactory = new SessionFactory(mailConfigurationMock);

        Session session = sessionFactory.createSession();

        assertNotNull(session);

        Properties props = session.getProperties();
        assertEquals(props.getProperty("mail.smtp.host"), "testHost");
        assertEquals(props.getProperty("mail.smtp.port"), "25");
    }

    @Test
    public void returnSingletonSession() {
        when(mailConfigurationMock.getHost()).thenReturn("testHost");
        when(mailConfigurationMock.getPort()).thenReturn("25");

        SessionFactory sessionFactory = new SessionFactory(mailConfigurationMock);

        Session session = sessionFactory.createSession();
        Session session2 = sessionFactory.createSession();

        assertNotNull(session);
        assertNotNull(session2);

        assertEquals(session, session2);
    }
}
