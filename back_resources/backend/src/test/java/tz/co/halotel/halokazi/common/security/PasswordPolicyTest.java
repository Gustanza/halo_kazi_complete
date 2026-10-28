package tz.co.halotel.halokazi.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import tz.co.halotel.halokazi.common.config.HalokaziProperties;
import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;

class PasswordPolicyTest {

    /** SettingsService stub returning the seed defaults (or an override for the min length). */
    private static PasswordPolicy policy(int minLength) {
        HalokaziProperties props = new HalokaziProperties(null, null, null,
                new HalokaziProperties.Settings(Duration.ofSeconds(60)));
        SettingsService settings = new SettingsService(null, Clock.systemUTC(), props) {
            @Override
            public int getInt(Setting setting) {
                return setting == Setting.PASSWORD_MIN_LENGTH ? minLength : setting.defaultValue();
            }
        };
        return new PasswordPolicy(settings);
    }

    private final PasswordPolicy policy = policy(8);

    @ParameterizedTest
    @ValueSource(strings = {"Str0ng!Pass", "Abcdef1!", "Aa1_aaaa", "Ästh3tic!", "P@ssw0rd-with-a-long-tail-1234567890"})
    void acceptsCompliantPasswords(String pw) {
        assertThat(policy.isSatisfiedBy(pw)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Abcde1!",      // 7 chars
            "str0ng!pass",  // no upper
            "STR0NG!PASS",  // no lower
            "Strong!Pass",  // no digit
            "Str0ngPass1",  // no special
            "Str0ng Pass",  // whitespace is not a special character
            ""
    })
    void rejectsEachMissingClassAndShortLength(String pw) {
        assertThat(policy.isSatisfiedBy(pw)).isFalse();
    }

    @Test
    void rejectsNull() {
        assertThat(policy.isSatisfiedBy(null)).isFalse();
    }

    @Test
    void minimumLengthComesFromSettings() {
        PasswordPolicy twelve = policy(12);
        assertThat(twelve.isSatisfiedBy("Abcdef1!xyz")).isFalse();   // 11
        assertThat(twelve.isSatisfiedBy("Abcdef1!xyzw")).isTrue();   // 12
        assertThat(twelve.describe()).contains("12");
        assertThat(policy.describe()).contains("at least 8").contains("uppercase").contains("special");
    }
}
