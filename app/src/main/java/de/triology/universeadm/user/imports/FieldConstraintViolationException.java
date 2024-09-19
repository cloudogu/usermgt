package de.triology.universeadm.user.imports;

import de.triology.universeadm.Constraint;
import de.triology.universeadm.EntityException;

import java.util.Arrays;

/**
 * FieldConstraintViolationException is a custom exception that (if thrown) will contain violated user field constraints.
 */
public class FieldConstraintViolationException extends EntityException {
    public final Constraint.ID[] violated;

    public FieldConstraintViolationException(final Constraint.ID... constraints) {
        super("Constraints violated: " + Arrays.toString(constraints));
        if (constraints.length == 0) {
            throw new IllegalArgumentException("There must be at least one constraint");
        }
        this.violated = constraints;
    }
}
