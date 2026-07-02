package de.triology.universeadm.multifactor;

/**
 * Simplified MFA credential for frontend consumption.
 */
public class MfaDTO {

    private String username;
    private String name;

    public MfaDTO() {
    }

    public MfaDTO(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
