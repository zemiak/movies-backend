package com.zemiak.movies.strings;

import java.text.Normalizer;

public final class Encodings {
    public static String toAscii(final String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String deAccent(String str) {
        return toAscii(str).replaceAll("[^\\x00-\\x7F]", "");
    }
}
