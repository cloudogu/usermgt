package de.triology.universeadm.robots;

/** Missing expiration preserves the previous value; explicit null removes it. */
public class RenewRobotRequest {
    private String expiredAt;
    private boolean expirationProvided;

    public String getExpiredAt() { return expiredAt; }
    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
        this.expirationProvided = true;
    }
    public boolean hasExpirationOverride() { return expirationProvided; }
}
