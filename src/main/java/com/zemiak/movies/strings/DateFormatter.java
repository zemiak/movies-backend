package com.zemiak.movies.strings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateFormatter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public static String format(LocalDateTime dt) {
        if (null == dt) {
            return null;
        }
        
        return formatter.format(dt);
    }

    public static LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, formatter);
    }
}
