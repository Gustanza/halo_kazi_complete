package tz.co.halotel.halokazi.auth.dto;

import java.time.OffsetDateTime;

/** Returned by login and successful phone verification. Send {@code token} as "Authorization: Bearer". */
public record AuthResponse(String token, OffsetDateTime expiresAt, long idleTimeoutSeconds, UserView user) {
}
