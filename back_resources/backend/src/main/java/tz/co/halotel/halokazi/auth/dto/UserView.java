package tz.co.halotel.halokazi.auth.dto;

import tz.co.halotel.halokazi.employer.EmployerVerificationStatus;
import tz.co.halotel.halokazi.user.AccountStatus;
import tz.co.halotel.halokazi.user.AppUser;
import tz.co.halotel.halokazi.user.Role;

/** The signed-in user as the UI sees it. {@code employerVerificationStatus} is null for non-employers. */
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
        return new UserView(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole(),
                user.getAccountStatus(), user.getPreferredLanguage(), employerVerificationStatus);
    }
}
