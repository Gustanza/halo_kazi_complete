package tz.co.halotel.halokazi.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.audit.AuditAction;
import tz.co.halotel.halokazi.audit.AuditService;
import tz.co.halotel.halokazi.audit.LoginAttemptService;
import tz.co.halotel.halokazi.audit.LoginFailureReason;
import tz.co.halotel.halokazi.auth.dto.AuthResponse;
import tz.co.halotel.halokazi.auth.dto.LoginRequest;
import tz.co.halotel.halokazi.auth.dto.UserView;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.employer.Employer;
import tz.co.halotel.halokazi.employer.EmployerRepository;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.IssuedSession;
import tz.co.halotel.halokazi.session.RevokeReason;
import tz.co.halotel.halokazi.session.SessionService;
import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;
import tz.co.halotel.halokazi.user.AppUser;
import tz.co.halotel.halokazi.user.AppUserRepository;

/** Login (AUTH-04, AUTH-13), logout and the current-user view. */
@Service
@BusinessTransaction
public class AuthService {

    private final AppUserRepository users;
    private final EmployerRepository employers;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessions;
    private final LoginAttemptService loginAttempts;
    private final AuditService audit;
    private final SettingsService settings;
    private final Clock clock;
    /** Compared against when the identifier is unknown, so response time does not reveal accounts. */
    private final String dummyPasswordHash;

    public AuthService(AppUserRepository users, EmployerRepository employers, PasswordEncoder passwordEncoder,
            SessionService sessions, LoginAttemptService loginAttempts, AuditService audit, SettingsService settings,
            Clock clock) {
        this.users = users;
        this.employers = employers;
        this.passwordEncoder = passwordEncoder;
        this.sessions = sessions;
        this.loginAttempts = loginAttempts;
        this.audit = audit;
        this.settings = settings;
        this.clock = clock;
        this.dummyPasswordHash = passwordEncoder.encode("halokazi-timing-equaliser");
    }

    /**
     * @throws ApiException INVALID_CREDENTIALS, ACCOUNT_LOCKED, PHONE_NOT_VERIFIED or ACCOUNT_DISABLED.
     *                      Counters and login_attempt/audit rows are committed regardless.
     */
    public AuthResponse login(LoginRequest request, ClientInfo client) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        String identifier = request.identifier();
        AppUser user = users.findByLoginIdentifier(identifier).orElse(null);

        if (user == null) {
            passwordEncoder.matches(request.password(), dummyPasswordHash);
            loginAttempts.failed(identifier, null, LoginFailureReason.UNKNOWN_IDENTIFIER, client);
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (user.isLocked(now)) {
            loginAttempts.failed(identifier, user, LoginFailureReason.ACCOUNT_LOCKED, client);
            throw locked(user, now);
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            boolean lockedNow = user.registerFailedLogin(settings.getInt(Setting.LOGIN_MAX_FAILED_ATTEMPTS),
                    settings.getMinutes(Setting.LOGIN_LOCK_MINUTES), now);
            loginAttempts.failed(identifier, user, LoginFailureReason.BAD_PASSWORD, client);
            throw lockedNow ? locked(user, now) : new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Correct password from here on: only the account state can still refuse entry.
        if (user.isPendingVerification()) {
            user.resetFailedLogins();
            loginAttempts.failed(identifier, user, LoginFailureReason.PHONE_NOT_VERIFIED, client);
            throw new ApiException(ErrorCode.PHONE_NOT_VERIFIED).detail("phone", user.getPhone());
        }
        if (!user.isActive()) {
            user.resetFailedLogins();
            loginAttempts.failed(identifier, user, LoginFailureReason.ACCOUNT_DISABLED, client);
            throw new ApiException(ErrorCode.ACCOUNT_DISABLED);
        }

        loginAttempts.succeeded(identifier, user, client);
        return signIn(user, client);
    }

    /** Opens a session for a user who has just proven who they are (password or OTP). */
    public AuthResponse signIn(AppUser user, ClientInfo client) {
        user.registerSuccessfulLogin(OffsetDateTime.now(clock));
        IssuedSession session = sessions.open(user, client);
        return new AuthResponse(session.token(), session.expiresAt(), session.idleTimeoutSeconds(), describe(user));
    }

    public void logout(AuthenticatedUser caller, ClientInfo client) {
        sessions.revoke(caller.sessionId(), RevokeReason.LOGOUT);
        users.findById(caller.userId()).ifPresent(user -> audit.success(AuditAction.LOGOUT, user, client));
    }

    @BusinessTransaction(readOnly = true)
    public UserView currentUser(AuthenticatedUser caller) {
        AppUser user = users.findById(caller.userId()).orElseThrow(() -> new ApiException(ErrorCode.UNAUTHENTICATED));
        return describe(user);
    }

    private UserView describe(AppUser user) {
        return UserView.of(user, employers.findByUserId(user.getId())
                .map(Employer::getVerificationStatus)
                .orElse(null));
    }

    private static ApiException locked(AppUser user, OffsetDateTime now) {
        long seconds = (long) Math.ceil(Duration.between(now, user.getLockedUntil()).toMillis() / 1000.0);
        return new ApiException(ErrorCode.ACCOUNT_LOCKED).retryAfter(seconds);
    }
}
