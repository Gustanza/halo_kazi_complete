package tz.co.halotel.halokazi.auth.dto;

import tz.co.halotel.halokazi.user.Role;

public record RegisterResponse(
        Long userId,
        String phone,
        Role role,
        long otpExpiresInSeconds,
        long resendAvailableInSeconds) {
}
