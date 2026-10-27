package tz.co.halotel.halokazi.session;

import tz.co.halotel.halokazi.user.Role;

/** The caller of the current request, resolved from its bearer token. Inject with {@link CurrentUser}. */
public record AuthenticatedUser(Long userId, Long sessionId, Role role) {
}
