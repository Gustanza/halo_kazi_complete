package tz.co.halotel.halokazi.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneNumbersTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "712345678",
            "712 345 678",
            "0712345678",
            "0712 345 678",
            "0712-345-678",
            "255712345678",
            "255 712 345 678",
            "+255712345678",
            "+255 712 345 678",
            "+255-712-345-678",
            "(0712) 345.678",
            "  +255712345678  ",
            "\t0712345678\n"
    })
    void normalisesEveryAcceptedFormatToE164(String raw) {
        assertThat(PhoneNumbers.normalize(raw)).contains("+255712345678");
        assertThat(PhoneNumbers.isValid(raw)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"612345678", "0612345678", "+255612345678", "255612345678", "621 000 111"})
    void acceptsSixPrefix(String raw) {
        assertThat(PhoneNumbers.normalize(raw)).hasValueSatisfying(v -> assertThat(v).startsWith("+2556").hasSize(13));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "   ",
            "812345678",          // prefix 8 is not mobile
            "512345678",
            "012345678",
            "71234567",           // 8 digits
            "7123456789",         // 10 digits
            "07123456789",        // 0 + 10 digits
            "071234567",          // 0 + 8 digits
            "+2557123456789",     // too long after +255
            "+25571234567",       // too short after +255
            "+254712345678",      // Kenya
            "254712345678",
            "+1 712 345 678",
            "+44712345678",
            "+2550712345678",     // trunk 0 after country code is not accepted
            "00255712345678",     // international 00 prefix is not accepted
            "0712-345-67a",
            "abc",
            "+255",
            "7123456７8"          // full-width digit
    })
    void rejectsInvalidNumbers(String raw) {
        assertThat(PhoneNumbers.normalize(raw)).isEmpty();
        assertThat(PhoneNumbers.isValid(raw)).isFalse();
    }

    @Test
    void requireThrowsOnInvalid() {
        assertThat(PhoneNumbers.require("0712345678")).isEqualTo("+255712345678");
        assertThatThrownBy(() -> PhoneNumbers.require("12345")).isInstanceOf(IllegalArgumentException.class);
    }
}
