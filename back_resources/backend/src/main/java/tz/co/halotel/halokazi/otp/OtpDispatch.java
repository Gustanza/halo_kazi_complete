package tz.co.halotel.halokazi.otp;

/** What the client needs after a code was sent: when it expires and when it may ask again. */
public record OtpDispatch(long expiresInSeconds, long resendAvailableInSeconds) {
}
