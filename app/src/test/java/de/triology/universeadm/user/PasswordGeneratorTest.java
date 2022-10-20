package de.triology.universeadm.user;

import org.junit.Test;

import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class PasswordGeneratorTest {

    @Test
    public void testPasswordGenerator() {
        Pattern specialCharacters = Pattern.compile("[^a-z0-9A-Z]");
        Pattern lowerCaseCharacters = Pattern.compile("[a-z]");
        Pattern upperCaseCharacters = Pattern.compile("[A-Z]");
        Pattern numberCharacters = Pattern.compile("[0-9]");

        for (int i = 0; i < 100; i++) {
            final String randomPassword = PasswordGenerator.random(30);
            final boolean hasSpecialCharacters = specialCharacters.matcher(randomPassword).find();
            assertTrue(hasSpecialCharacters);

            final boolean hasLowerCaseCharacters = lowerCaseCharacters.matcher(randomPassword).find();
            assertTrue(hasLowerCaseCharacters);

            final boolean hasUpperCaseCharacters = upperCaseCharacters.matcher(randomPassword).find();
            assertTrue(hasUpperCaseCharacters);

            final boolean hasNumberCharacters = numberCharacters.matcher(randomPassword).find();
            assertTrue(hasNumberCharacters);

            assertEquals(30, randomPassword.length());
        }
    }
}
