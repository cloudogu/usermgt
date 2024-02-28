package de.triology.universeadm.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.mail.*;
import org.eclipse.angus.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class MailSender {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private static final int MAX_RETRIES = 10;
    private static final int MAX_DELAY_MS = 60 * 60 * 1000; // Maximum delay in milliseconds (60 minutes)
    private static final int RETRY_INTERVAL_SECONDS = 30;
    private final Map<RetryableMessage, Long> retryMessages = new ConcurrentHashMap<>();

    private final SessionFactory sessionFactory;

    @Inject
    public MailSender(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(this::retry, RETRY_INTERVAL_SECONDS, RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public boolean send(Message msg) {
        Optional<Address[]> recipients = getRecipients(msg);
        Optional<String> subject = getSubject(msg);

        if (!recipients.isPresent() || !subject.isPresent()) {
            logger.error("Recipient or Subject is empty");
            return false;
        }

        Session session = Optional.ofNullable(msg.getSession()).orElseGet(sessionFactory::createSession);
        if (!checkConnection(session)){
            RetryableMessage retryableMessage = new RetryableMessage(recipients.get(), subject.get(), session, msg);
            retryMessages.put(retryableMessage, System.currentTimeMillis() + retryableMessage.delay);
            return false;
        }

        List<String> maskedEmails = getMaskedMails(recipients.get());

        try {
            Transport transport = session.getTransport();
            transport.sendMessage(msg, recipients.get());
        } catch (MessagingException e) {
            logger.warn("Could not send mail with subject {} to user {}, retry later", subject.get(), maskedEmails);
            RetryableMessage retryableMessage = new RetryableMessage(recipients.get(), subject.get(), session, msg);
            retryMessages.put(retryableMessage, System.currentTimeMillis() + retryableMessage.delay);

            return false;
        }

        logger.info("Successfully sent mail with subject {} to user(s) {}", subject.get(), maskedEmails);
        return true;
    }

    private boolean checkConnection(Session session) {
        try {
            SMTPTransport transport = (SMTPTransport) session.getTransport();

            if (transport.isConnected()){
                logger.debug("Connection to mail server is established");
                return true;
            }

            logger.debug("No connection to mail server - try reconnect");
            session.getTransport().connect();
            return true;
        } catch (MessagingException e) {
            logger.debug(
                "No connection to mail server, try later",
                e.getCause()
            );
            return false;
        }
    }

    private void retry() {
        logger.trace("Retry method called");

        if (retryMessages.isEmpty()){
            return;
        }

        logger.info("Try to resend {} messages", retryMessages.size());

        Long now = System.currentTimeMillis();

        for (Map.Entry<RetryableMessage, Long> entry : retryMessages.entrySet()) {
            if (entry.getValue() > now){
                return;
            }

            RetryableMessage msg = entry.getKey();

            List<String> maskedEmails = this.getMaskedMails(msg.recipients);

            logger.debug("Retry to send mail to user(s) {}", maskedEmails);

            if (msg.retrySend()) {
                logger.debug("Successfully sent mail to user(s) {} after {} retries", maskedEmails, msg.retryCounter);
                retryMessages.remove(msg);
                return;
            }

            if (msg.retryCounter > MAX_RETRIES) {
                logger.error("Could not send mail to user(s) {} after {} retries, remove message from retry queue", maskedEmails, MAX_RETRIES);
                retryMessages.remove(msg);
            }

            msg.increaseDelay();
            entry.setValue(System.currentTimeMillis() + msg.delay);

            logger.debug("Retry to send mail to user(s) {} failed: current retry counter {}, increased delay to {}", maskedEmails, msg.retryCounter, msg.delay);
        }
    }

    private List<String> getMaskedMails(Address[] recipients) {
        return Arrays.stream(recipients)
                .map(Address::toString)
                .map(this::maskEmail)
                .collect(Collectors.toList());
    }

    private Optional<Address[]> getRecipients(Message msg) {
        try {
            return Optional.of(msg.getAllRecipients());
        } catch (MessagingException e) {
            logger.warn("Invalid addresses in recipients", e);
        }

        return Optional.empty();
    }

    private Optional<String> getSubject(Message msg) {
        try {
            return Optional.of(msg.getSubject());
        } catch (MessagingException e) {
            logger.warn("Could not get subject from message", e);
        }

        return Optional.empty();
    }

    private String maskEmail(String email) {
        int subStringLength = 5;

        if (email.length() <= 5) {
            subStringLength = 1;
        }

        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < email.length(); i++) {
            if (i < subStringLength) {
                masked.append(email.charAt(i)); // Append original characters before the subStringLength
            } else {
                masked.append('*'); // Append '*' for characters after the subStringLength
            }
        }

        return masked.toString();
    }

    private class RetryableMessage {
        private final Address[] recipients;
        private final String subject;
        private final Session session;
        private final Message message;
        private int retryCounter;
        private int delay;

        public RetryableMessage(Address[] recipients, String subject, Session session, Message message) {
            this.recipients = recipients;
            this.subject = subject;
            this.session = session;
            this.message = message;
            this.retryCounter = 1;
            this.delay = RETRY_INTERVAL_SECONDS;
        }

        public void increaseDelay() {
            delay = Math.min(delay * 2, MAX_DELAY_MS);
        }

        public boolean retrySend() {
            try {
                session.getTransport().sendMessage(message, recipients);
                return true;
            } catch (MessagingException e) {
                retryCounter++;

                logger.warn("Retry for mail with subject {} for user {} failed, fail counter: {}", subject, getMaskedMails(recipients), retryCounter);
                return false;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RetryableMessage that = (RetryableMessage) o;
            return Arrays.equals(recipients, that.recipients) && Objects.equals(subject, that.subject) && Objects.equals(session, that.session) && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(subject, session, message);
            result = 31 * result + Arrays.hashCode(recipients);
            return result;
        }
    }
}
