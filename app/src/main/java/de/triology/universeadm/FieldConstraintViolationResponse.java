package de.triology.universeadm;

import de.triology.universeadm.user.imports.FieldConstraintViolationException;

public class FieldConstraintViolationResponse {
  public final Constraint.ID[] constraints;

  public FieldConstraintViolationResponse(final FieldConstraintViolationException e) {
    this.constraints = e.violated;
  }
}
