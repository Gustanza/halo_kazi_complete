package tz.co.halotel.halokazi.account;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tz.co.halotel.halokazi.common.validation.StrongPassword;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfile.Notifications;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfile.Privacy;
import tz.co.halotel.halokazi.lookup.LookupRef;

/** Request and response shapes of the job seeker Settings screen (GET/PUT /api/me/settings/**). */
public final class SettingsDtos {

    private SettingsDtos() {
    }

    /** Everything the Settings screen shows. */
    public record SettingsView(Account account, Notifications notifications, Privacy privacy, Preferences preferences) {
    }

    /** Read-only here; the profile editor fills date of birth, gender, location and "about". */
    public record Account(String fullName, String email, String phone, LocalDate dateOfBirth, String gender,
            Location location, String about) {
    }

    /** Any part may be null until the profile is completed. */
    public record Location(LookupRef region, String district, String address) {
    }

    /** "EN" | "SW". */
    public record Preferences(String language) {
    }

    // Booleans are boxed so a missing field is a validation error instead of silently "false".
    public record NotificationsRequest(@NotNull Boolean email, @NotNull Boolean applicationUpdates,
            @NotNull Boolean jobAlerts, @NotNull Boolean newsletter) {

        Notifications toChoice() {
            return new Notifications(email, applicationUpdates, jobAlerts, newsletter);
        }
    }

    public record PrivacyRequest(@NotNull Boolean profileVisible, @NotNull Boolean showEmail,
            @NotNull Boolean showPhone) {

        Privacy toChoice() {
            return new Privacy(profileVisible, showEmail, showPhone);
        }
    }

    public record LanguageRequest(
            @NotNull @Pattern(regexp = "(?i)EN|SW", message = "Use EN (English) or SW (Kiswahili).") String language) {
    }

    /** AUTH-05: change the password of a signed-in user. */
    public record ChangePasswordRequest(
            @NotBlank @Size(max = 64) String currentPassword,
            @NotBlank @Size(max = 64) @StrongPassword String newPassword,
            @NotBlank String confirmPassword) {

        @Override
        public String toString() {
            return "ChangePasswordRequest[***]";
        }
    }
}
