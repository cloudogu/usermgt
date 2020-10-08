package de.triology.universeadm;

public class ConstraintViolationResponse {
  public final Constraint.ID[] constraints;

  public ConstraintViolationResponse(final ConstraintViolationException e) {
    this.constraints = e.violated;
  }
}
