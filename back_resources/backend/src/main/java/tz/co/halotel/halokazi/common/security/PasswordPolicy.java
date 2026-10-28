package tz.co.halotel.halokazi.common.security;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;

/** AUTH-06: minimum length (from SYSTEM_SETTING) plus upper, lower, digit and special character. */
@Component
public class PasswordPolicy {

    private static final Pattern UPPER = Pattern.compile("\\p{Lu}");
    private static final Pattern LOWER = Pattern.compile("\\p{Ll}");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern SPECIAL = Pattern.compile("[^\\p{L}\\d\\s]");

    private final SettingsService settings;

    public PasswordPolicy(SettingsService settings) {
        this.settings = settings;
    }

    public boolean isSatisfiedBy(String password) {
        return password != null
                && password.length() >= minLength()
                && UPPER.matcher(password).find()
                && LOWER.matcher(password).find()
                && DIGIT.matcher(password).find()
                && SPECIAL.matcher(password).find();
    }

    public String describe() {
        return "Password must be at least " + minLength()
                + " characters and include an uppercase letter, a lowercase letter, a number and a special character.";
    }

    private int minLength() {
        return settings.getInt(Setting.PASSWORD_MIN_LENGTH);
    }
}
