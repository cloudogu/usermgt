package de.triology.universeadm;

public class ConstraintViolationResponse {
  public Constraint.Type[] constraints;

  public ConstraintViolationResponse(final ConstraintViolationException e) {
    this.constraints = e.violated;
  }
}
