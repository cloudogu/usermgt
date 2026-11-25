package de.triology.universeadm.robots;

import java.util.Optional;

/** PAT metadata only; the token secret is never returned by the robot overview. */
public class RobotPAT {
    private String id;
    private String userId;
    private String displayName;
    private String createdAt;
    private String expiresAt;
    private String scope;
    private String owner;
    private String ownerDisplayName;
    private boolean ownerDeleted;
    private String description;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getCreatedAt() { return createdAt; }
    public String getExpiresAt() { return expiresAt; }
    public String getScope() { return scope; }
    public String getOwner() { return owner; }
    public String getOwnerDisplayName() { return ownerDisplayName; }
    public boolean isOwnerDeleted() { return ownerDeleted; }
    public String getDescription() { return description; }

    void setOwnerDisplayName(Optional<String> ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName.orElse(owner);
        this.ownerDeleted = !ownerDisplayName.isPresent();
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
