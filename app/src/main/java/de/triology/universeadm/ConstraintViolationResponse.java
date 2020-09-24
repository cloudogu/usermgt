package de.triology.universeadm;

public class ConstraintViolationResponse {
  public Constraint.ID[] constraints;

  public ConstraintViolationResponse(final ConstraintViolationException e) {
    this.constraints = e.violated;
  }
}
