package de.triology.universeadm.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mail")
@XmlAccessorType(XmlAccessType.FIELD)
public class MailConfiguration {

    @XmlElement(name = "host")
    private String host;
    @XmlElement(name = "port")
    private String port;

    @XmlElement(name = "from")
    private String from;

    @XmlElement(name = "subject")
    private String subject;

    @XmlElement(name = "message")
    private String message;

    @XmlElement(name = "maxRetries")
    private int maxRetries;

    @XmlElement(name = "maxRetryDelay")
    private int maxRetryDelay;

    @XmlElement(name = "retryInterval")
    private int retryInterval;

    public MailConfiguration() {}

    public MailConfiguration(String host, String port, String from, String subject, String message, int maxRetries, int maxRetryDelay, int retryInterval) {
        this.host = host;
        this.port = port;
        this.from = from;
        this.subject = subject;
        this.message = message;
        this.maxRetries = maxRetries;
        this.maxRetryDelay = maxRetryDelay;
        this.retryInterval = retryInterval;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getMaxRetryDelay() {
        return maxRetryDelay;
    }

    public int getRetryInterval() {
        return retryInterval;
    }
}
