package tz.co.halotel.halokazi.session;

import java.time.Duration;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tz.co.halotel.halokazi.user.AppUser;

/** USER_SESSION row: server-side session behind an opaque bearer token (AUTH-12). */
@Entity
@Table(name = "user_session")
class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private AppUser user;

    @Column(name = "token_hash", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "ip_address", updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", updatable = false)
    private String userAgent;

    @Column(name = "last_activity_at", nullable = false)
    private OffsetDateTime lastActivityAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revoke_reason")
    private RevokeReason revokeReason;

    protected UserSession() {
    }

    static UserSession open(AppUser user, String tokenHash, String ipAddress, String userAgent, OffsetDateTime now,
            OffsetDateTime expiresAt) {
        UserSession session = new UserSession();
        session.user = user;
        session.tokenHash = tokenHash;
        session.ipAddress = ipAddress;
        session.userAgent = userAgent;
        session.lastActivityAt = now;
        session.expiresAt = expiresAt;
        return session;
    }

    boolean isRevoked() {
        return revokedAt != null;
    }

    boolean isPastAbsoluteExpiry(OffsetDateTime now) {
        return !expiresAt.isAfter(now);
    }

    boolean isIdle(Duration idleTimeout, OffsetDateTime now) {
        return !lastActivityAt.plus(idleTimeout).isAfter(now);
    }

    /** Slides the idle window, writing at most once per {@code writeInterval}. */
    void touch(Duration writeInterval, OffsetDateTime now) {
        if (!lastActivityAt.plus(writeInterval).isAfter(now)) {
            lastActivityAt = now;
        }
    }

    void revoke(RevokeReason reason, OffsetDateTime now) {
        if (revokedAt == null) {
            revokedAt = now;
            revokeReason = reason;
        }
    }

    Long getId() {
        return id;
    }

    AppUser getUser() {
        return user;
    }

    OffsetDateTime getExpiresAt() {
        return expiresAt;
    }
}
