package de.triology.universeadm.configreader;

import javax.xml.bind.annotation.XmlElement;

public class ApplicationConfiguration {
    @XmlElement(name = "sender-mail")
    String senderMail;
    @XmlElement
    String subject;
    @XmlElement
    String content;
    @XmlElement(name = "admin-group")
    String adminGroup;
    @XmlElement(name = "manager-group")
    String managerGroup;

    @XmlElement
    String host;

    @XmlElement
    String port;

    public String getSenderMail() {
        return senderMail;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getAdminGroup() {
        return adminGroup;
    }

    public String getManagerGroup() {
        return managerGroup;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }
}
