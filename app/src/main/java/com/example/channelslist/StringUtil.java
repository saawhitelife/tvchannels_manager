package com.example.channelslist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private final static String SPECIAL_SYMBOLS = "[^a-zA-Z0-9А-Яа-я\\s]";
    private final static String ONLY_DIGITS = "([^0-9])";

    public final static int MAXIMUM_CHANNEL_NUMBER_LENGTH = 4;
    public final static int MINIMUM_CHANNEL_NUMBER_LENGTH = 1;
    public final static int MINIMUM_TV_NAME_LENGTH = 3;

    public static boolean detectUnwantedSymbols(String string) {
        Matcher m = Pattern.compile(SPECIAL_SYMBOLS).matcher(string);
        return m.find();
    }

    public static boolean containsOnlyDigitsNoSpaces(String string) {
        Matcher m = Pattern.compile(ONLY_DIGITS).matcher(string);
        return !m.find();
    }
}
