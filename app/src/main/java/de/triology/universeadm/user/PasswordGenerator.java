package de.triology.universeadm.user;

import com.google.common.base.Joiner;

import java.util.*;

public final class PasswordGenerator {
    public static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String SPECIAL = "~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
    public static final String ALL = LOWER_CASE + UPPER_CASE + NUMBERS + SPECIAL;

    public static String random(int length) {
        if (length < 4){
            throw new IllegalArgumentException("Length may not be smaller than 4");
        }

        List<Character> chars = new ArrayList<>();

        chars.add(getRandomCharInString(LOWER_CASE));
        chars.add(getRandomCharInString(UPPER_CASE));
        chars.add(getRandomCharInString(NUMBERS));
        chars.add(getRandomCharInString(SPECIAL));

        for (int i = 0; i < length-4; i++) {
            chars.add(getRandomCharInString(ALL));
        }

        Collections.shuffle(chars);
        return  Joiner.on("").join(chars);
    }

    private static char getRandomCharInString(String characters){
        Random random = new Random(System.nanoTime());
        final int position = random.nextInt(characters.length());
        return characters.charAt(position);
    }
}

