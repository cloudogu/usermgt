package de.triology.universeadm.robots;

/** Missing fields preserve the previous values; explicit null clears them. */
public class RenewRobotRequest {
    private String expiresAt;
    private boolean expirationProvided;
    private String owner;
    private boolean ownerProvided;
    private String description;
    private boolean descriptionProvided;

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
        this.expirationProvided = true;
    }
    public boolean hasExpirationOverride() { return expirationProvided; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) {
        this.owner = owner;
        this.ownerProvided = true;
    }
    public boolean hasOwnerOverride() { return ownerProvided; }
    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.descriptionProvided = true;
    }
    public boolean hasDescriptionOverride() { return descriptionProvided; }
}
