package de.triology.universeadm.user;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
    private static final String ALL = LOWER_CASE + UPPER_CASE + NUMBERS + SPECIAL;
    private static final String ILLEGAL_CONDITION_COMBINATION_EXCEPTION_MESSAGE = "Length may not be smaller than 4";

    public String random(int length) {
        if (length < 4) {
            throw new IllegalArgumentException(ILLEGAL_CONDITION_COMBINATION_EXCEPTION_MESSAGE);
        }

        List<Character> chars = new ArrayList<>();

        chars.add(getRandomCharInString(LOWER_CASE));
        chars.add(getRandomCharInString(UPPER_CASE));
        chars.add(getRandomCharInString(NUMBERS));
        chars.add(getRandomCharInString(SPECIAL));

        for (int i = 0; i < length - 4; i++) {
            chars.add(getRandomCharInString(ALL));
        }

        Collections.shuffle(chars);
        return Joiner.on("").join(chars);
    }

    private static char getRandomCharInString(String characters) {
        Random random = new Random(System.nanoTime());
        final int position = random.nextInt(characters.length());
        return characters.charAt(position);
    }
}

