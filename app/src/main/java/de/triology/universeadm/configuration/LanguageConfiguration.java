package de.triology.universeadm.configuration;

import javax.xml.bind.annotation.XmlElement;

public class LanguageConfiguration {
    @XmlElement(name = "starting-protocol")
    String startingProtocol;
    @XmlElement(name = "ending-protocol")
    String endingProtocol;
    @XmlElement(name = "csv-with-lines-read-successful")
    String csvWithLinesReadSuccessful;
    @XmlElement(name = "added-successful")
    String addedSuccessful;
    @XmlElement(name = "incomplete-line")
    String incompleteLine;
    @XmlElement(name = "user-already-exists")
    String userAlreadyExists;
    @XmlElement(name = "error-on-creating-user")
    String errorOnCreatingUser;
    @XmlElement(name = "empty-username")
    String emptyUsername;
    @XmlElement(name = "empty-displayname")
    String emptyDisplayname;
    @XmlElement(name = "empty-surname")
    String emptySurname;
    @XmlElement(name = "empty-mail")
    String emptyMail;
    @XmlElement(name = "user-added")
    String userAdded;
    @XmlElement(name = "user-part-of-group-already")
    String userPartOfGroupAlready;
    @XmlElement(name = "group-does-not-exist")
    String groupDoesNotExist;
    @XmlElement(name = "could-not-send-mail")
    String couldNotSendMail;
    @XmlElement(name = "unknown-error")
    String unknownError;
    @XmlElement(name = "unique-mail-error")
    String uniqueMailError;

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
