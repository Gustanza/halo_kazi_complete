package tz.co.halotel.halokazi.session;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.common.config.HalokaziProperties;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.security.TokenHasher;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;
import tz.co.halotel.halokazi.user.AppUser;

/**
 * Opaque-token sessions (AUTH-12). A token is valid while not revoked, before its absolute
 * expiry, and used within SESSION_IDLE_TIMEOUT_MINUTES of the previous request.
 */
@Service
@BusinessTransaction
public class SessionService {

    private final UserSessionRepository repository;
    private final SettingsService settings;
    private final HalokaziProperties.Session config;
    private final Clock clock;

    public SessionService(UserSessionRepository repository, SettingsService settings, HalokaziProperties properties,
            Clock clock) {
        this.repository = repository;
        this.settings = settings;
        this.config = properties.session();
        this.clock = clock;
    }

    public IssuedSession open(AppUser user, ClientInfo client) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        String token = TokenHasher.newToken();
        UserSession session = repository.save(UserSession.open(user, TokenHasher.sha256Hex(token), client.ip(),
                client.userAgent(), now, now.plus(config.absoluteTtl())));
        return new IssuedSession(token, session.getExpiresAt(), idleTimeout().toSeconds());
    }

    /**
     * Resolves a bearer token and slides its idle window.
     *
     * @throws ApiException UNAUTHENTICATED, SESSION_EXPIRED or ACCOUNT_DISABLED
     */
    public AuthenticatedUser authenticate(String token) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        UserSession session = repository.findByTokenHashWithUser(TokenHasher.sha256Hex(token))
                .filter(s -> !s.isRevoked())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHENTICATED));

        if (session.isPastAbsoluteExpiry(now)) {
            throw new ApiException(ErrorCode.SESSION_EXPIRED);
        }
        if (session.isIdle(idleTimeout(), now)) {
            session.revoke(RevokeReason.IDLE_TIMEOUT, now);
            throw new ApiException(ErrorCode.SESSION_EXPIRED);
        }
        AppUser user = session.getUser();
        if (!user.isActive()) {
            throw new ApiException(ErrorCode.ACCOUNT_DISABLED);
        }
        session.touch(config.activityWriteInterval(), now);
        return new AuthenticatedUser(user.getId(), session.getId(), user.getRole());
    }

    public void revoke(Long sessionId, RevokeReason reason) {
        repository.findById(sessionId).ifPresent(s -> s.revoke(reason, OffsetDateTime.now(clock)));
    }

    /** Ends every other open session of the user, e.g. other devices after a password change. */
    public int revokeOthers(Long userId, Long keepSessionId, RevokeReason reason) {
        return repository.revokeOthers(userId, keepSessionId, reason, OffsetDateTime.now(clock));
    }

    /** Ends every open session of the user, e.g. after a password reset. */
    public int revokeAll(Long userId, RevokeReason reason) {
        return repository.revokeAll(userId, reason, OffsetDateTime.now(clock));
    }

    private Duration idleTimeout() {
        return settings.getMinutes(Setting.SESSION_IDLE_TIMEOUT_MINUTES);
    }
}
