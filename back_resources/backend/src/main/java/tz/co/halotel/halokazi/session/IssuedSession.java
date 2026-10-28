package tz.co.halotel.halokazi.session;

import java.time.OffsetDateTime;

/** A freshly opened session. {@code token} is the only copy of the raw bearer token. */
public record IssuedSession(String token, OffsetDateTime expiresAt, long idleTimeoutSeconds) {
}
