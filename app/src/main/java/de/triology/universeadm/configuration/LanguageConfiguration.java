package de.triology.universeadm.configuration;

import javax.xml.bind.annotation.XmlElement;

public class LanguageConfiguration {
    @XmlElement(name = "starting-protocol")
    private String startingProtocol;
    @XmlElement(name = "ending-protocol")
    private String endingProtocol;
    @XmlElement(name = "csv-with-lines-read-successful")
    private String csvWithLinesReadSuccessful;
    @XmlElement(name = "added-successful")
    private String addedSuccessful;
    @XmlElement(name = "incomplete-line")
    private String incompleteLine;
    @XmlElement(name = "user-already-exists")
    private String userAlreadyExists;
    @XmlElement(name = "error-on-creating-user")
    private String errorOnCreatingUser;
    @XmlElement(name = "empty-username")
    private String emptyUsername;
    @XmlElement(name = "empty-displayname")
    private String emptyDisplayname;
    @XmlElement(name = "empty-surname")
    private String emptySurname;
    @XmlElement(name = "empty-mail")
    private String emptyMail;
    @XmlElement(name = "user-added")
    private String userAdded;
    @XmlElement(name = "user-part-of-group-already")
    private String userPartOfGroupAlready;
    @XmlElement(name = "group-does-not-exist")
    private String groupDoesNotExist;
    @XmlElement(name = "could-not-send-mail")
    private String couldNotSendMail;
    @XmlElement(name = "unknown-error")
    private String unknownError;
    @XmlElement(name = "unique-mail-error")
    private String uniqueMailError;

    public String getStartingProtocol() {
        return startingProtocol;
    }

    public String getEndingProtocol() {
        return endingProtocol;
    }

    public String getCsvWithLinesReadSuccessful() {
        return csvWithLinesReadSuccessful;
    }

    public String getAddedSuccessful() {
        return addedSuccessful;
    }

    public String getIncompleteLine() {
        return incompleteLine;
    }

    public String getUserAlreadyExists() {
        return userAlreadyExists;
    }

    public String getErrorOnCreatingUser() {
        return errorOnCreatingUser;
    }

    public String getEmptyUsername() {
        return emptyUsername;
    }

    public String getEmptyDisplayname() {
        return emptyDisplayname;
    }

    public String getEmptySurname() {
        return emptySurname;
    }

    public String getEmptyMail() {
        return emptyMail;
    }

    public String getUserAdded() {
        return userAdded;
    }

    public String getUserPartOfGroupAlready() {
        return userPartOfGroupAlready;
    }

    public String getGroupDoesNotExist() {
        return groupDoesNotExist;
    }

    public String getCouldNotSendMail() {
        return couldNotSendMail;
    }

    public String getUnknownError() {
        return unknownError;
    }

    public String getUniqueMailError() {
        return uniqueMailError;
    }
}
