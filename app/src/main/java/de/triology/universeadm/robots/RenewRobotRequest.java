package de.triology.universeadm.robots;

/** Optional expiration override; null preserves the previous PAT's expiration. */
public class RenewRobotRequest {
    private String expiredAt;

    public String getExpiredAt() { return expiredAt; }
    public void setExpiredAt(String expiredAt) { this.expiredAt = expiredAt; }
}
