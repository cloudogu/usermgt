package de.triology.universeadm.robots;

/** Uses the same request fields as PAT creation. */
public class CreateRobotRequest {
    private String displayName;
    private String expiresAt;
    private String scope;
    private String owner;
    private String description;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
