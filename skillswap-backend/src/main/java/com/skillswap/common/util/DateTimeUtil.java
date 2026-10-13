package com.skillswap.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static final String DEFAULT_DATE_TIME_PATTERN =
            "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);

    /**
     * Returns the current local date and time.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Returns the current date and time for a specific timezone.
     */
    public static LocalDateTime now(String zoneId) {
        return ZonedDateTime.now(
                ZoneId.of(zoneId)
        ).toLocalDateTime();
    }

    /**
     * Formats LocalDateTime using the default format.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Formats LocalDateTime using a custom pattern.
     */
    public static String format(
            LocalDateTime dateTime,
            String pattern
    ) {
        if (dateTime == null) {
            return null;
        }

        if (pattern == null || pattern.isBlank()) {
            return format(dateTime);
        }

        return dateTime.format(
                DateTimeFormatter.ofPattern(pattern)
        );
    }

    /**
     * Parses a date/time string using the default format.
     */
    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(
                value,
                DEFAULT_FORMATTER
        );
    }

    /**
     * Parses a date/time string using a custom pattern.
     */
    public static LocalDateTime parse(
            String value,
            String pattern
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        if (pattern == null || pattern.isBlank()) {
            return parse(value);
        }

        return LocalDateTime.parse(
                value,
                DateTimeFormatter.ofPattern(pattern)
        );
    }

    /**
     * Checks whether the supplied date/time is in the past.
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(now());
    }

    /**
     * Checks whether the supplied date/time is in the future.
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(now());
    }
}