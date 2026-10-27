package tz.co.halotel.halokazi.common.util;

import java.util.Locale;

/** Emails are stored trimmed and lower-cased, matching the LOWER(email) unique index. */
public final class Emails {

    private Emails() {
    }

    public static String normalize(String raw) {
        return raw == null ? null : raw.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean looksLikeEmail(String identifier) {
        return identifier != null && identifier.contains("@");
    }
}
