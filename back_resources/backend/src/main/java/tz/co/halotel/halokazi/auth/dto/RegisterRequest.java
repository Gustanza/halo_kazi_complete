package tz.co.halotel.halokazi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tz.co.halotel.halokazi.common.validation.PasswordsMatch;
import tz.co.halotel.halokazi.common.validation.StrongPassword;
import tz.co.halotel.halokazi.common.validation.TanzanianPhone;

/**
 * AUTH-01 / AUTH-07. {@code fullName} is the company name for employers. Email is required for
 * employers only - job seekers register and log in with phone number alone, and
 * {@link tz.co.halotel.halokazi.auth.RegistrationService} assigns them a synthetic internal
 * address so the (NOT NULL, unique) {@code app_user.email} column is still satisfied.
 */
@PasswordsMatch
public record RegisterRequest(
        @NotNull RegistrationRole role,
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @TanzanianPhone String phone,
        @Email(message = "Enter a valid email address.") @Size(max = 254) String email,
        // BCrypt only reads the first 72 bytes; 64 chars keeps multi-byte input safe.
        @NotBlank @Size(max = 64) @StrongPassword String password,
        @NotBlank String repeatPassword) implements PasswordsMatch.PasswordConfirmation {

    @Override
    public String toString() {
        return "RegisterRequest[role=" + role + ", phone=" + phone + ", email=" + email + "]";
    }
}
