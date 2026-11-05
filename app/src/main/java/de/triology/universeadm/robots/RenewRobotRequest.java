package de.triology.universeadm.robots;

/** Optional expiration override when replacing a robot PAT. */
public class RenewRobotRequest {
    private String expiredAt;

    public String getExpiredAt() { return expiredAt; }
    public void setExpiredAt(String expiredAt) { this.expiredAt = expiredAt; }
}
