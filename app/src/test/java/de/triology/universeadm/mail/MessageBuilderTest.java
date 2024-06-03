package de.triology.universeadm.mail;

import static org.junit.Assert.*;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import org.junit.*;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MessageBuilderTest {

    @Test
    public void testBuildMessage() throws MessagingException, IOException {
        // Arrange
        String from = "from@example.com";
        String to = "to@example.com";
        String subject = "Test Subject";
        String content = "Hello World!";

        MessageBuilder builder = new MessageBuilder(Session.getInstance(new Properties()))
            .from(from)
            .to(to)
            .subject(subject)
            .content(content);

        // Act
        Optional<Message> messageOptional = builder.build();
        // Assert
        assertTrue(messageOptional.isPresent());

        Message message = messageOptional.get();
        assertEquals(from, message.getFrom()[0].toString());
        assertEquals(to, message.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(subject, message.getSubject());
        assertEquals(content, message.getContent());
    }

    @Test
    public void testBuildMessageWithoutContent() {
        // Arrange
        String from = "from@example.com";
        String to = "to@example.com";
        String subject = "Test Subject";

        MessageBuilder builder = new MessageBuilder(Session.getInstance(new Properties()))
            .from(from)
            .to(to)
            .subject(subject);

        // Act
        assertFalse(builder.build().isPresent());
    }

    @Test
    public void testBuildMessageWithoutSubject() {
        // Arrange
        String from = "from@example.com";
        String to = "to@example.com";
        String content = "Hello World!";

        MessageBuilder builder = new MessageBuilder(Session.getInstance(new Properties()))
            .from(from)
            .to(to)
            .content(content);

        // Act
        assertFalse(builder.build().isPresent());
    }

    @Test
    public void testBuildMessageWithoutSender() {
        // Arrange
        String to = "to@example.com";
        String subject = "Test Subject";
        String content = "Hello World!";

        MessageBuilder builder = new MessageBuilder(Session.getInstance(new Properties()))
            .to(to)
            .subject(subject)
            .content(content);

        // Act
        assertFalse(builder.build().isPresent());
    }

    @Test
    public void testBuildMessageWithoutRecipient() {
        // Arrange
        String from = "from@example.com";
        String subject = "Test Subject";
        String content = "Hello World!";

        MessageBuilder builder = new MessageBuilder(Session.getInstance(new Properties()))
            .from(from)
            .subject(subject)
            .content(content);

        // Act
        assertFalse(builder.build().isPresent());
    }
}
