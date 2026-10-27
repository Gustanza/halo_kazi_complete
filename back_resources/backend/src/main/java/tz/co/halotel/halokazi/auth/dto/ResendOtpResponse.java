package tz.co.halotel.halokazi.auth.dto;

/** Deliberately identical whether or not the phone belongs to an account awaiting verification. */
public record ResendOtpResponse(String message, long otpExpiresInSeconds, long resendAvailableInSeconds) {

    public static final String MESSAGE =
            "If this number has an account awaiting verification, a new code has been sent.";
}
