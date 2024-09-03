package de.triology.universeadm;

import de.triology.universeadm.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class ValidMailConstraintTest {

    @Test
    public void testValidMailWithNumbers() {
        final ValidMailConstraint validator = new ValidMailConstraint();
        final User user = new User();

        user.setMail("test1337@mail.de");
        assertFalse(validator.violatedBy(user, null));
        user.setMail("test@mail24.de");
        assertFalse(validator.violatedBy(user, null));
        user.setMail("test1337@mail24.de");
        assertFalse(validator.violatedBy(user, null));
    }

    @Test
    public void testValidMailWithUmlauts() {
        final ValidMailConstraint validator = new ValidMailConstraint();
        final User user = new User();

        user.setMail("täst@mail.de");
        assertFalse(validator.violatedBy(user, null));
        user.setMail("test@mäil.de");
        assertFalse(validator.violatedBy(user, null));
        user.setMail("töst@mäil.de");
        assertFalse(validator.violatedBy(user, null));
    }

    @Test
    public void testValidMailWithNewTLDs() {
        final ValidMailConstraint validator = new ValidMailConstraint();
        final User user = new User();

        user.setMail("test@mail.cool");
        assertFalse(validator.violatedBy(user, null));
        user.setMail("test@mail.germany");
        assertFalse(validator.violatedBy(user, null));
        user.setMail("test@mail.dev");
        assertFalse(validator.violatedBy(user, null));
    }

    @Test
    public void testValidMailWithoutTLD() {
        final ValidMailConstraint validator = new ValidMailConstraint();
        final User user = new User();

        user.setMail("test@mail");
        assertFalse(validator.violatedBy(user, null));
    }

    @Test
    public void testInvalidMail() {
        final ValidMailConstraint validator = new ValidMailConstraint();
        final User user = new User();

        user.setMail("Hello");
        assertTrue(validator.violatedBy(user, null));
        user.setMail("test@");
        assertTrue(validator.violatedBy(user, null));
        user.setMail("@mail.de");
        assertTrue(validator.violatedBy(user, null));
    }
}
