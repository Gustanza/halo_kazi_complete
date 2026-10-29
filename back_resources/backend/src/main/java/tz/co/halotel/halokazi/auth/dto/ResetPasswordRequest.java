package tz.co.halotel.halokazi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tz.co.halotel.halokazi.common.validation.StrongPassword;

/** AUTH-05: the SMS code sent by /password/forgot plus the new password. */
public record ResetPasswordRequest(
        @NotBlank @Size(max = 254) String identifier,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "Enter the 6-digit code.") String code,
        @NotBlank @Size(max = 64) @StrongPassword String newPassword,
        @NotBlank String confirmPassword) {

    @Override
    public String toString() {
        return "ResetPasswordRequest[identifier=" + identifier + "]";
    }
}
