package tz.co.halotel.halokazi.user;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * APP_USER - login identity shared by every role. Only the columns the auth flows use are mapped;
 * the rest keep their database defaults.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified;

    @Column(name = "phone_verified_at")
    private OffsetDateTime phoneVerifiedAt;

    @Column(name = "preferred_language", nullable = false)
    private String preferredLanguage;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private OffsetDateTime passwordChangedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AppUser() {
    }

    /** A self-registered account, awaiting SMS phone verification (AUTH-02). */
    public static AppUser register(String fullName, String email, String phone, String passwordHash, Role role,
            OffsetDateTime now) {
        AppUser user = new AppUser();
        user.fullName = fullName;
        user.email = email;
        user.phone = phone;
        user.passwordHash = passwordHash;
        user.role = role;
        user.accountStatus = AccountStatus.PENDING_VERIFICATION;
        user.phoneVerified = false;
        user.preferredLanguage = "EN";
        user.failedLoginCount = 0;
        user.passwordChangedAt = now;
        return user;
    }

    public void markPhoneVerified(OffsetDateTime now) {
        this.phoneVerified = true;
        this.phoneVerifiedAt = now;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    public boolean isLocked(OffsetDateTime now) {
        return lockedUntil != null && lockedUntil.isAfter(now);
    }

    /**
     * Counts a wrong password; on reaching {@code maxAttempts} the account is locked for
     * {@code lockDuration} and the counter restarts (AUTH-13).
     *
     * @return true if this failure locked the account
     */
    public boolean registerFailedLogin(int maxAttempts, Duration lockDuration, OffsetDateTime now) {
        failedLoginCount++;
        if (failedLoginCount >= maxAttempts) {
            lockedUntil = now.plus(lockDuration);
            failedLoginCount = 0;
            return true;
        }
        return false;
    }

    public void registerSuccessfulLogin(OffsetDateTime now) {
        failedLoginCount = 0;
        lockedUntil = null;
        lastLoginAt = now;
    }

    /** Correct password but not allowed in: clear the brute-force counter, keep everything else. */
    public void resetFailedLogins() {
        failedLoginCount = 0;
    }

    /** AUTH-05: the caller has already proven the current password. */
    public void changePassword(String newPasswordHash, OffsetDateTime now) {
        passwordHash = newPasswordHash;
        passwordChangedAt = now;
    }

    /** A completed password reset proves the person holds the phone, so any login lock is lifted. */
    public void resetPassword(String newPasswordHash, OffsetDateTime now) {
        changePassword(newPasswordHash, now);
        failedLoginCount = 0;
        lockedUntil = null;
    }

    /** "EN" or "SW" (ck_app_user_lang). */
    public void changePreferredLanguage(String language) {
        preferredLanguage = language;
    }

    public boolean isPendingVerification() {
        return accountStatus == AccountStatus.PENDING_VERIFICATION;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE && deletedAt == null;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    public OffsetDateTime getLockedUntil() {
        return lockedUntil;
    }
}
