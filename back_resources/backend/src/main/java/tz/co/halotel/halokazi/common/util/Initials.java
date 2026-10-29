package tz.co.halotel.halokazi.common.util;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/** Badge text for organisations without a logo: "CRDB Bank PLC" -> "CRDB", "Vodacom Tanzania" -> "VT". */
public final class Initials {

    private static final int MAX = 4;

    private Initials() {
    }

    public static String of(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        String[] words = name.trim().split("\\s+");
        String first = words[0].replaceAll("[^\\p{L}\\d]", "");
        // An acronym-style first word (CRDB, NMB) is already the brand.
        if (first.length() >= 2 && first.length() <= MAX && first.equals(first.toUpperCase(Locale.ROOT))) {
            return first;
        }
        return Arrays.stream(words)
                .map(w -> w.replaceAll("[^\\p{L}\\d]", ""))
                .filter(w -> !w.isEmpty())
                .limit(3)
                .map(w -> w.substring(0, 1).toUpperCase(Locale.ROOT))
                .collect(Collectors.joining());
    }
}
