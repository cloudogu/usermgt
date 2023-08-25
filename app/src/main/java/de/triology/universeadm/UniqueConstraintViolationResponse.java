package de.triology.universeadm;

public class UniqueConstraintViolationResponse {
  public final Constraint.ID[] constraints;

  public UniqueConstraintViolationResponse(final UniqueConstraintViolationException e) {
    this.constraints = e.violated;
  }
}
