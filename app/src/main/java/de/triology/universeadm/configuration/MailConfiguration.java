package de.triology.universeadm.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mail")
@XmlAccessorType(XmlAccessType.FIELD)
public class MailConfiguration {

    @XmlElement(name = "host")
    private final String host;
    @XmlElement(name = "port")
    private final String port;

    @XmlElement(name = "from")
    private final String from;

    @XmlElement(name = "subject")
    private final String subject;

    @XmlElement(name = "message")
    private final String message;

    public MailConfiguration(String host, String port, String from, String subject, String message) {
        this.host = host;
        this.port = port;
        this.from = from;
        this.subject = subject;
        this.message = message;
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
}
