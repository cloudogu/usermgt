package de.triology.universeadm.robots;

/** PAT metadata only; the token secret is never returned by the robot overview. */
public class RobotPAT {
    private String id;
    private String userId;
    private String displayName;
    private String createdAt;
    private String expiresAt;
    private String scope;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getCreatedAt() { return createdAt; }
    public String getExpiresAt() { return expiresAt; }
    public String getScope() { return scope; }

    void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
