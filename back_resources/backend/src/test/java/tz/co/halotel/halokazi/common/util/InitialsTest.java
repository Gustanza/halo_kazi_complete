package tz.co.halotel.halokazi.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InitialsTest {

    @ParameterizedTest
    @CsvSource({
            "CRDB Bank PLC, CRDB",
            "NMB Bank PLC, NMB",
            "Vodacom Tanzania, VT",
            "Yas, Y",
            "Kilimanjaro Lodges Ltd, KLL",
            "Afya Bora Hospital Group, ABH",
            "'  airtel   tanzania ', AT",
            "'(TTCL) Corporation', TTCL"
    })
    void initials(String name, String expected) {
        assertThat(Initials.of(name)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = { "''", "'   '" })
    void blank(String name) {
        assertThat(Initials.of(name)).isEmpty();
        assertThat(Initials.of(null)).isEmpty();
    }
}
