package de.triology.universeadm.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.configuration.MailConfiguration;
import jakarta.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Singleton
public class MailSender {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private final int maxRetries;
    private final int maxDelayMs;
    private final int retryIntervalSeconds;
    private final Map<RetryableMessage, Long> retryMessages = new ConcurrentHashMap<>();
    private final AtomicReference<Transport> transportRef;
    private  final ExecutorService sendTaskExecutor;

    @Inject
    public MailSender(SessionFactory sessionFactory, MailConfiguration mailConfiguration){
        try {
            Transport transport = sessionFactory.createSession().getTransport();
            this.transportRef = new AtomicReference<>(transport);
        } catch (NoSuchProviderException e) {
            throw new IllegalArgumentException("No SMTP provider exists - cannot initialize Transport for mail", e);
        }

        maxRetries = mailConfiguration.getMaxRetries();
        maxDelayMs = mailConfiguration.getMaxRetryDelay() * 1000;
        retryIntervalSeconds = mailConfiguration.getRetryInterval();

        sendTaskExecutor = Executors.newCachedThreadPool();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::retry, retryIntervalSeconds, retryIntervalSeconds, TimeUnit.SECONDS);
    }

    public void send(Message msg) {
        Optional<Address[]> recipients = getRecipients(msg);
        Optional<String> subject = getSubject(msg);

        if (!recipients.isPresent() || !subject.isPresent()) {
            logger.error("Recipient or Subject is empty");
            return;
        }

        if (isDisconnected()){
            RetryableMessage retryableMessage = new RetryableMessage(recipients.get(), subject.get(), msg);
            retryMessages.put(retryableMessage, System.currentTimeMillis() + retryableMessage.delay);
            return;
        }

        List<String> maskedEmails = getMaskedMails(recipients.get());

        CompletableFuture.runAsync(() -> {
            try {
                transportRef.get().sendMessage(msg, recipients.get());
            } catch (MessagingException e) {
                logger.warn("Could not send mail with subject {} to user {}, retry later", subject.get(), maskedEmails, e);
                RetryableMessage retryableMessage = new RetryableMessage(recipients.get(), subject.get(), msg);
                retryMessages.put(retryableMessage, System.currentTimeMillis() + retryableMessage.delay);
            }

            logger.info("Successfully sent mail with subject {} to user(s) {}", subject.get(), maskedEmails);
        }, sendTaskExecutor);
    }

    public int getRetryMessageCount() {
        return retryMessages.size();
    }

    private boolean isDisconnected() {
        try {
            if (transportRef.get().isConnected()){
                logger.debug("Connection to mail server is established");
                return false;
            }

            logger.debug("No connection to mail server - try reconnect");
            transportRef.get().connect();
            logger.debug("Reconnected to mail server");
            return false;
        } catch (MessagingException e) {
            logger.warn("Could not connect to mail server",e);
            return true;
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

            if (msg.retryCounter > maxRetries) {
                logger.error("Could not send mail to user(s) {} after {} retries, remove message from retry queue", maskedEmails, maxRetries);
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
        private final Message message;
        private int retryCounter;
        private int delay;

        public RetryableMessage(Address[] recipients, String subject, Message message) {
            this.recipients = recipients;
            this.subject = subject;
            this.message = message;
            this.retryCounter = 1;
            this.delay = retryIntervalSeconds;
        }

        public void increaseDelay() {
            delay = Math.min(delay * 2, maxDelayMs);
        }

        public boolean retrySend() {
            if (isDisconnected()){
                return false;
            }

            try {
                transportRef.get().sendMessage(message, recipients);
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
            return Arrays.equals(recipients, that.recipients) && Objects.equals(subject, that.subject) && Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(subject, message);
            result = 31 * result + Arrays.hashCode(recipients);
            return result;
        }
    }
}
