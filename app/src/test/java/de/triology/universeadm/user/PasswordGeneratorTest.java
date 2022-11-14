package de.triology.universeadm.user;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PasswordGeneratorTest {
    private static final String SPECIAL_CASE_REGEX = "[^a-z0-9A-Z]";
    private static final String LOWER_CASE_REGEX = "[a-z]";
    private static final String UPPERCASE_REGEX = "[A-Z]";
    private static final String NUMBER_REGEX = "[0-9]";

    @Test
    public void testPasswordGenerator() {
        Pattern specialCharacters = Pattern.compile(SPECIAL_CASE_REGEX);
        Pattern lowerCaseCharacters = Pattern.compile(LOWER_CASE_REGEX);
        Pattern upperCaseCharacters = Pattern.compile(UPPERCASE_REGEX);
        Pattern numberCharacters = Pattern.compile(NUMBER_REGEX);
        PasswordGenerator pwdGen = new PasswordGenerator();

        for (int i = 0; i < 100; i++) {
            final String randomPassword = pwdGen.random(30);
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
