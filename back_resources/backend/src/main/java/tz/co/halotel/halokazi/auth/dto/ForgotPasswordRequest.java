package tz.co.halotel.halokazi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** AUTH-05: {@code identifier} is the phone number or email of the account. */
public record ForgotPasswordRequest(@NotBlank @Size(max = 254) String identifier) {
}
