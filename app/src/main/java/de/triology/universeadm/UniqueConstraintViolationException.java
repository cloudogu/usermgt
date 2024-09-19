package de.triology.universeadm;

import java.util.Arrays;

/**
 * @author Simon Klein <sklein@cloudogu.com>
 */
public class UniqueConstraintViolationException extends EntityException {
  public final Constraint.ID[] violated;

  public UniqueConstraintViolationException(final Constraint.ID... constraints) {
    super("Constraints violated: " + Arrays.toString(constraints));
    this.violated = constraints;
  }
}
