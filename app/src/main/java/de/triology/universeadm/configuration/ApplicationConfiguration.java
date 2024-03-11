package de.triology.universeadm.configuration;

import javax.xml.bind.annotation.XmlElement;

public class ApplicationConfiguration {
    @XmlElement(name = "admin-group")
    String adminGroup;
    @XmlElement(name = "manager-group")
    String managerGroup;

    public String getAdminGroup() {
        return adminGroup;
    }

    public String getManagerGroup() {
        return managerGroup;
    }
}
