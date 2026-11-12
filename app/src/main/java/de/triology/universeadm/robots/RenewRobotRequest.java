package de.triology.universeadm.robots;

/** Missing expiration preserves the previous value; explicit null removes it. */
public class RenewRobotRequest {
    private String expiresAt;
    private boolean expirationProvided;

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
        this.expirationProvided = true;
    }
    public boolean hasExpirationOverride() { return expirationProvided; }
}
