package tz.co.halotel.halokazi.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TokenHasherTest {

    @Test
    void tokensAreRandomUrlSafeAnd256Bit() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 2_000; i++) {
            String token = TokenHasher.newToken();
            assertThat(token).hasSize(43).matches("[A-Za-z0-9_-]+");
            assertThat(seen.add(token)).as("duplicate token").isTrue();
        }
    }

    @Test
    void sha256IsStableKnownVectorAndHex() {
        assertThat(TokenHasher.sha256Hex("abc"))
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        String token = TokenHasher.newToken();
        assertThat(TokenHasher.sha256Hex(token)).isEqualTo(TokenHasher.sha256Hex(token))
                .hasSize(64).matches("[0-9a-f]+").isNotEqualTo(token);
        assertThat(TokenHasher.sha256Hex(token)).isNotEqualTo(TokenHasher.sha256Hex(token + "x"));
    }
}
