package com.skillswap.common.util;

public final class StringUtil {

    private StringUtil() {
    }

    /**
     * Checks whether a string is null or empty after trimming.
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Checks whether a string contains actual text.
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Trims a string safely.
     */
    public static String trim(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }

    /**
     * Returns an empty string instead of null.
     */
    public static String defaultIfNull(String value) {
        return value == null ? "" : value;
    }

    /**
     * Returns the supplied default when the value is empty.
     */
    public static String defaultIfEmpty(
            String value,
            String defaultValue
    ) {
        return isEmpty(value) ? defaultValue : value;
    }

    /**
     * Limits a string to the specified maximum length.
     */
    public static String truncate(
            String value,
            int maxLength
    ) {
        if (value == null) {
            return null;
        }

        if (maxLength <= 0) {
            return "";
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    /**
     * Capitalizes the first character.
     */
    public static String capitalize(String value) {
        if (isEmpty(value)) {
            return value;
        }

        return Character.toUpperCase(value.charAt(0))
                + value.substring(1);
    }

    /**
     * Converts a string to lowercase safely.
     */
    public static String lowerCase(String value) {
        return value == null ? null : value.toLowerCase();
    }

    /**
     * Converts a string to uppercase safely.
     */
    public static String upperCase(String value) {
        return value == null ? null : value.toUpperCase();
    }
}