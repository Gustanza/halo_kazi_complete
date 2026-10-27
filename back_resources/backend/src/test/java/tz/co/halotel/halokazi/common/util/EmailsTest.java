package tz.co.halotel.halokazi.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailsTest {

    @Test
    void normaliseTrimsAndLowerCases() {
        assertThat(Emails.normalize("  John.DOE@Example.COM ")).isEqualTo("john.doe@example.com");
        assertThat(Emails.normalize("a@b.tz")).isEqualTo("a@b.tz");
        assertThat(Emails.normalize(null)).isNull();
    }

    @Test
    void normaliseIsLocaleIndependent() {
        // Turkish dotless-i trap: ROOT locale must be used whatever the JVM default is.
        java.util.Locale previous = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.forLanguageTag("tr-TR"));
            assertThat(Emails.normalize("INFO@X.TZ")).isEqualTo("info@x.tz");
        } finally {
            java.util.Locale.setDefault(previous);
        }
    }

    @Test
    void looksLikeEmailOnlyWhenItHasAnAt() {
        assertThat(Emails.looksLikeEmail("a@b.tz")).isTrue();
        assertThat(Emails.looksLikeEmail("0712345678")).isFalse();
        assertThat(Emails.looksLikeEmail("+255712345678")).isFalse();
        assertThat(Emails.looksLikeEmail(null)).isFalse();
    }
}
