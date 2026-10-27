package tz.co.halotel.halokazi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** AUTH-04: {@code identifier} is a phone number (any accepted format) or an email address. */
public record LoginRequest(
        @NotBlank @Size(max = 254) String identifier,
        @NotBlank @Size(max = 64) String password) {

    @Override
    public String toString() {
        return "LoginRequest[identifier=" + identifier + "]";
    }
}
