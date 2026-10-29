package tz.co.halotel.halokazi.auth.dto;

import tz.co.halotel.halokazi.employer.EmployerVerificationStatus;
import tz.co.halotel.halokazi.user.AccountStatus;
import tz.co.halotel.halokazi.user.AppUser;
import tz.co.halotel.halokazi.user.Role;

/**
 * The signed-in user as the UI sees it. {@code employerVerificationStatus} is null for non-employers.
 * {@code email} is null for job seekers - they register and log in with phone number alone, and
 * {@link tz.co.halotel.halokazi.user.AppUser#getEmail()} is a synthetic, internal-only address for
 * them (see {@link tz.co.halotel.halokazi.auth.RegistrationService}), never a real contact address.
 */
public record UserView(
        Long userId,
        String fullName,
        String email,
        String phone,
        Role role,
        AccountStatus accountStatus,
        String preferredLanguage,
        EmployerVerificationStatus employerVerificationStatus) {

    public static UserView of(AppUser user, EmployerVerificationStatus employerVerificationStatus) {
        String email = user.getRole() == Role.EMPLOYER ? user.getEmail() : null;
        return new UserView(user.getId(), user.getFullName(), email, user.getPhone(), user.getRole(),
                user.getAccountStatus(), user.getPreferredLanguage(), employerVerificationStatus);
    }
}
