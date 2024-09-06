package de.triology.universeadm.user.imports;

import de.triology.universeadm.Constraint;
import org.apache.shiro.util.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldConstraintViolationExceptionTest {
    @Test
    public void classShouldImplementRuntimeException() {
        Assert.isAssignable(RuntimeException.class, FieldConstraintViolationException.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ConstructorShouldThrowExceptionOnEmptyArgs() {
        new FieldConstraintViolationException();
    }

    @Test
    public void ConstructorShouldNotThrowExceptionArgumentList() {
        FieldConstraintViolationException sut = new FieldConstraintViolationException(Constraint.ID.UNIQUE_EMAIL);
        assertEquals(Constraint.ID.UNIQUE_EMAIL, sut.violated[0]);

        FieldConstraintViolationException sut2 = new FieldConstraintViolationException(Constraint.ID.VALID_EMAIL, Constraint.ID.UNIQUE_USERNAME);
        assertEquals(Constraint.ID.VALID_EMAIL, sut2.violated[0]);
        assertEquals(Constraint.ID.UNIQUE_USERNAME, sut2.violated[1]);
    }
}
