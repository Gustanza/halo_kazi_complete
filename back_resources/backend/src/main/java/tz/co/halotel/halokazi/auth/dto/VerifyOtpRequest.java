package tz.co.halotel.halokazi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import tz.co.halotel.halokazi.common.validation.TanzanianPhone;

public record VerifyOtpRequest(
        @NotBlank @TanzanianPhone String phone,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "Enter the 6-digit code.") String code) {

    @Override
    public String toString() {
        return "VerifyOtpRequest[phone=" + phone + "]";
    }
}
