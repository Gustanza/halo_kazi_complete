package tz.co.halotel.halokazi.account;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.account.SettingsDtos.Account;
import tz.co.halotel.halokazi.account.SettingsDtos.ChangePasswordRequest;
import tz.co.halotel.halokazi.account.SettingsDtos.Location;
import tz.co.halotel.halokazi.account.SettingsDtos.Preferences;
import tz.co.halotel.halokazi.account.SettingsDtos.SettingsView;
import tz.co.halotel.halokazi.audit.AuditAction;
import tz.co.halotel.halokazi.audit.AuditResult;
import tz.co.halotel.halokazi.audit.AuditService;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfile.Notifications;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfile.Privacy;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfileRepository;
import tz.co.halotel.halokazi.lookup.LookupRef;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.RevokeReason;
import tz.co.halotel.halokazi.session.SessionService;
import tz.co.halotel.halokazi.user.AppUser;
import tz.co.halotel.halokazi.user.AppUserRepository;

/** The signed-in user's own settings and password. */
@Service
@BusinessTransaction
public class AccountSettingsService {

    private final AppUserRepository users;
    private final JobSeekerProfileRepository profiles;
    private final SessionService sessions;
    private final PasswordEncoder encoder;
    private final AuditService audit;
    private final JdbcClient jdbc;
    private final Clock clock;

    public AccountSettingsService(AppUserRepository users, JobSeekerProfileRepository profiles,
            SessionService sessions, PasswordEncoder encoder, AuditService audit, JdbcClient jdbc, Clock clock) {
        this.users = users;
        this.profiles = profiles;
        this.sessions = sessions;
        this.encoder = encoder;
        this.audit = audit;
        this.jdbc = jdbc;
        this.clock = clock;
    }

    public SettingsView settings(AuthenticatedUser caller) {
        return jdbc.sql("""
                SELECT u.full_name, u.phone, u.preferred_language,
                       p.date_of_birth, p.gender, p.professional_summary, p.address_line,
                       p.notify_email, p.notify_application_updates, p.receive_job_alerts, p.notify_newsletter,
                       p.profile_visible, p.show_email, p.show_phone,
                       r.code r_code, r.name_en r_en, r.name_sw r_sw, d.name_en d_name
                  FROM app_user u
                  JOIN job_seeker_profile p ON p.user_id = u.user_id
                  LEFT JOIN region r ON r.region_id = p.region_id
                  LEFT JOIN district d ON d.district_id = p.district_id
                 WHERE u.user_id = :userId
                """)
                .param("userId", caller.userId())
                .query((rs, n) -> {
                    var dob = rs.getObject("date_of_birth", java.time.LocalDateTime.class);
                    var location = new Location(
                            LookupRef.ofNullable(rs.getString("r_code"), rs.getString("r_en"), rs.getString("r_sw")),
                            rs.getString("d_name"), rs.getString("address_line"));
                    // This endpoint is job-seeker only (the JOIN below throws JOB_SEEKERS_ONLY for anyone
                    // else), and job seekers only have a synthetic, internal-only email (see
                    // RegistrationService) - never show it as if it were a real contact address.
                    return new SettingsView(
                            new Account(rs.getString("full_name"), null, rs.getString("phone"),
                                    dob == null ? null : dob.toLocalDate(), rs.getString("gender"), location,
                                    rs.getString("professional_summary")),
                            new Notifications(rs.getBoolean("notify_email"), rs.getBoolean("notify_application_updates"),
                                    rs.getBoolean("receive_job_alerts"), rs.getBoolean("notify_newsletter")),
                            new Privacy(rs.getBoolean("profile_visible"), rs.getBoolean("show_email"),
                                    rs.getBoolean("show_phone")),
                            new Preferences(rs.getString("preferred_language")));
                })
                .optional()
                .orElseThrow(() -> new ApiException(ErrorCode.JOB_SEEKERS_ONLY));
    }

    public SettingsView updateNotifications(AuthenticatedUser caller, Notifications choice, ClientInfo client) {
        profileOf(caller).updateNotifications(choice);
        return saved(caller, "notifications", client);
    }

    public SettingsView updatePrivacy(AuthenticatedUser caller, Privacy choice, ClientInfo client) {
        profileOf(caller).updatePrivacy(choice);
        return saved(caller, "privacy", client);
    }

    public SettingsView updateLanguage(AuthenticatedUser caller, String language, ClientInfo client) {
        profileOf(caller); // settings are a job seeker feature
        userOf(caller).changePreferredLanguage(language.toUpperCase(Locale.ROOT));
        return saved(caller, "language", client);
    }

    /**
     * AUTH-05. Other devices are signed out (their sessions revoked as PASSWORD_CHANGED); the session that
     * made the change stays signed in.
     *
     * @throws ApiException VALIDATION_FAILED (confirmPassword / newPassword) or CURRENT_PASSWORD_INCORRECT
     */
    public void changePassword(AuthenticatedUser caller, ChangePasswordRequest request, ClientInfo client) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED).field("confirmPassword", "Passwords do not match.");
        }
        AppUser user = userOf(caller);
        if (!encoder.matches(request.currentPassword(), user.getPasswordHash())) {
            audit.record(AuditAction.PASSWORD_CHANGE_FAILED, AuditResult.FAILURE, user, client, null);
            throw new ApiException(ErrorCode.CURRENT_PASSWORD_INCORRECT)
                    .field("currentPassword", ErrorCode.CURRENT_PASSWORD_INCORRECT.defaultMessage());
        }
        if (encoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED)
                    .field("newPassword", "Choose a password that is different from your current one.");
        }
        user.changePassword(encoder.encode(request.newPassword()), OffsetDateTime.now(clock));
        int signedOut = sessions.revokeOthers(user.getId(), caller.sessionId(), RevokeReason.PASSWORD_CHANGED);
        audit.record(AuditAction.PASSWORD_CHANGED, AuditResult.SUCCESS, user, client,
                Map.of("otherSessionsEnded", signedOut));
    }

    private JobSeekerProfile profileOf(AuthenticatedUser caller) {
        return profiles.findByUserId(caller.userId()).orElseThrow(() -> new ApiException(ErrorCode.JOB_SEEKERS_ONLY));
    }

    private AppUser userOf(AuthenticatedUser caller) {
        return users.findById(caller.userId()).orElseThrow(() -> new ApiException(ErrorCode.UNAUTHENTICATED));
    }

    /** Writes the change before re-reading with plain SQL, then records it. */
    private SettingsView saved(AuthenticatedUser caller, String section, ClientInfo client) {
        users.flush();
        audit.record(AuditAction.SETTINGS_UPDATED, AuditResult.SUCCESS, userOf(caller), client,
                Map.of("section", section));
        return settings(caller);
    }
}
