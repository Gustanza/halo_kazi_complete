package tz.co.halotel.halokazi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import tz.co.halotel.halokazi.common.validation.TanzanianPhone;

public record ResendOtpRequest(@NotBlank @TanzanianPhone String phone) {
}
