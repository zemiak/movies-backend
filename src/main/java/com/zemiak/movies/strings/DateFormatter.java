package com.zemiak.movies.strings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateFormatter {
    public static String format(LocalDateTime dt) {
        return DateTimeFormatter.ISO_DATE_TIME.format(dt);
    }
}
