package de.triology.universeadm.user.imports;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public class CSVUserDTO {
    @CsvBindByName(required = true)
    private String username;

    @CsvBindByName(required = true)
    private String displayname;

    @CsvBindByName(required = true)
    private String givenname;

    @CsvBindByName(required = true)
    private String surname;

    @CsvBindByName(required = true)
    private String mail;

    @CsvBindByName(required = true)
    private boolean pwdReset;

    @CsvBindByName(required = true)
    private boolean external;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isPwdReset() {
        return pwdReset;
    }

    public void setPwdReset(boolean pwdReset) {
        this.pwdReset = pwdReset;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    @Override
    public String toString() {
        return "CSVUserDTO{" +
                "username='" + username + '\'' +
                ", displayname='" + displayname + '\'' +
                ", givenname='" + givenname + '\'' +
                ", surname='" + surname + '\'' +
                ", mail='" + mail + '\'' +
                ", pwdReset=" + pwdReset +
                ", external=" + external +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSVUserDTO that = (CSVUserDTO) o;
        return pwdReset == that.pwdReset && external == that.external && Objects.equals(username, that.username) && Objects.equals(displayname, that.displayname) && Objects.equals(givenname, that.givenname) && Objects.equals(surname, that.surname) && Objects.equals(mail, that.mail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, displayname, givenname, surname, mail, pwdReset, external);
    }
}
