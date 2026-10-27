package tz.co.halotel.halokazi.otp;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * OTP_CODE row. One row covers a resend control window: a resend replaces the code on the
 * same row and bumps resend_count (AUTH-03). Only the BCrypt hash of the code is stored.
 */
@Entity
@Table(name = "otp_code")
class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "phone", nullable = false, updatable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, updatable = false)
    private OtpPurpose purpose;

    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "resend_count", nullable = false)
    private int resendCount;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "consumed_at")
    private OffsetDateTime consumedAt;

    @Column(name = "ip_address", updatable = false)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected OtpCode() {
    }

    static OtpCode issue(Long userId, String phone, OtpPurpose purpose, String codeHash, OffsetDateTime expiresAt,
            String ipAddress) {
        OtpCode otp = new OtpCode();
        otp.userId = userId;
        otp.phone = phone;
        otp.purpose = purpose;
        otp.codeHash = codeHash;
        otp.expiresAt = expiresAt;
        otp.ipAddress = ipAddress;
        return otp;
    }

    /** Replaces the code after a resend; the new code gets a fresh set of attempts. */
    void reissue(String newCodeHash, OffsetDateTime newExpiresAt) {
        codeHash = newCodeHash;
        expiresAt = newExpiresAt;
        resendCount++;
        attemptCount = 0;
    }

    void recordFailedAttempt() {
        attemptCount++;
    }

    void consume(OffsetDateTime now) {
        consumedAt = now;
    }

    boolean isExpired(OffsetDateTime now) {
        return !expiresAt.isAfter(now);
    }

    /** The table has no "sent at" column; every send sets expires_at = sent + TTL. */
    OffsetDateTime lastSentAt(Duration ttl) {
        return expiresAt.minus(ttl);
    }

    String getCodeHash() {
        return codeHash;
    }

    int getResendCount() {
        return resendCount;
    }

    int getAttemptCount() {
        return attemptCount;
    }

    OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
