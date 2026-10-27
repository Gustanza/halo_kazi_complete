package tz.co.halotel.halokazi.common.util;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Tanzanian mobile numbers. Accepts what people actually type - "7XX XXX XXX", "07XXXXXXXX",
 * "2557XXXXXXXX", "+2557XXXXXXXX", with spaces or dashes - and produces E.164 ("+255" + 9 digits,
 * first digit 6 or 7), the only form stored in the database.
 */
public final class PhoneNumbers {

    private static final String COUNTRY_CODE = "255";
    private static final Pattern SEPARATORS = Pattern.compile("[\\s\\-.()]");
    private static final Pattern SUBSCRIBER = Pattern.compile("[67]\\d{8}");

    private PhoneNumbers() {
    }

    public static Optional<String> normalize(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String digits = SEPARATORS.matcher(raw.trim()).replaceAll("");
        String subscriber;
        if (digits.startsWith("+" + COUNTRY_CODE)) {
            subscriber = digits.substring(4);
        } else if (digits.startsWith(COUNTRY_CODE) && digits.length() == 12) {
            subscriber = digits.substring(3);
        } else if (digits.startsWith("0") && digits.length() == 10) {
            subscriber = digits.substring(1);
        } else {
            subscriber = digits;
        }
        return SUBSCRIBER.matcher(subscriber).matches()
                ? Optional.of("+" + COUNTRY_CODE + subscriber)
                : Optional.empty();
    }

    public static boolean isValid(String raw) {
        return normalize(raw).isPresent();
    }

    /** For input that has already passed {@code @TanzanianPhone} validation. */
    public static String require(String raw) {
        return normalize(raw).orElseThrow(() -> new IllegalArgumentException("Invalid phone number"));
    }
}
