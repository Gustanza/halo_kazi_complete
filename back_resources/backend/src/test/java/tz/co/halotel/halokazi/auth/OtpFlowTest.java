package tz.co.halotel.halokazi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;

import tz.co.halotel.halokazi.auth.dto.ResendOtpResponse;
import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

/** AUTH-02, AUTH-03, AUTH-13 for OTP verify / resend. */
class OtpFlowTest extends AbstractIntegrationTest {

    private static String wrong(String code) {
        return code.equals("000000") ? "111111" : "000000";
    }

    /** Pretend the last send happened 31 s earlier, so the 30 s cooldown has elapsed (code still valid). */
    private void skipCooldown(long userId) {
        exec("update otp_code set expires_at = expires_at - interval '31' second "
                + "where user_id = ? and consumed_at is null", userId);
    }

    @Test
    void wrongCodeDecrementsRemainingAttempts() throws Exception {
        Registered r = register("JOB_SEEKER");
        for (int remaining = 4; remaining >= 1; remaining--) {
            postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", wrong(r.code())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("OTP_INVALID"))
                    .andExpect(jsonPath("$.details.remainingAttempts").value(remaining));
        }
        assertThat(queryInt("select attempt_count from otp_code where user_id = ?", r.userId())).isEqualTo(4);
        assertThat(auditActions(r.userId())).filteredOn("OTP_VERIFY_FAILED"::equals).hasSize(4);
        // still pending
        assertThat(queryString("select account_status from app_user where user_id = ?", r.userId()))
                .isEqualTo("PENDING_VERIFICATION");
        // the right code still works on the 5th try
        verify(r.phone(), r.code());
    }

    @Test
    void fiveWrongCodesLockTheCode_evenTheCorrectCodeIsRejected() throws Exception {
        Registered r = register("JOB_SEEKER");
        for (int i = 0; i < 4; i++) {
            postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", wrong(r.code())))
                    .andExpect(status().isBadRequest());
        }
        postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", wrong(r.code())))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("OTP_ATTEMPTS_EXCEEDED"));
        postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", r.code()))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("OTP_ATTEMPTS_EXCEEDED"));
        assertThat(queryString("select account_status from app_user where user_id = ?", r.userId()))
                .isEqualTo("PENDING_VERIFICATION");
        assertThat(queryInt("select attempt_count from otp_code where user_id = ?", r.userId())).isEqualTo(5);

        // a resend issues a fresh code with fresh attempts
        skipCooldown(r.userId());
        postJson("/api/auth/otp/resend", Map.of("phone", r.phone())).andExpect(status().isAccepted());
        verify(r.phone(), sms.lastCodeFor(r.phone()));
    }

    @Test
    void expiredCodeIsRejected() throws Exception {
        Registered r = register("JOB_SEEKER");
        exec("update otp_code set expires_at = systimestamp - interval '1' second where user_id = ?", r.userId());
        postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", r.code()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("OTP_EXPIRED"));
        assertThat(queryString("select account_status from app_user where user_id = ?", r.userId()))
                .isEqualTo("PENDING_VERIFICATION");
    }

    @Test
    void codeStillValidJustBeforeTwoMinutes() throws Exception {
        Registered r = register("JOB_SEEKER");
        exec("update otp_code set expires_at = systimestamp + interval '5' second where user_id = ?", r.userId());
        verify(r.phone(), r.code());
    }

    @Test
    void verifyForUnknownOrAlreadyVerifiedPhoneIsInvalid() throws Exception {
        postJson("/api/auth/otp/verify", Map.of("phone", newPhone(), "code", "123456"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("OTP_INVALID"));

        Registered r = register("JOB_SEEKER");
        verify(r.phone(), r.code());
        postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", r.code()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("OTP_INVALID"));
    }

    @Test
    void verifyValidation() throws Exception {
        postJson("/api/auth/otp/verify", Map.of("phone", newPhone(), "code", "12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("code"));
        postJson("/api/auth/otp/verify", Map.of("phone", "123", "code", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("phone"));
    }

    @Test
    void resendWithinCooldownIsRejectedWithRetryAfter() throws Exception {
        Registered r = register("JOB_SEEKER");
        postJson("/api/auth/otp/resend", Map.of("phone", r.phone()))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("OTP_RESEND_TOO_SOON"))
                .andExpect(jsonPath("$.retryAfterSeconds").value(allOf(greaterThan(0), lessThanOrEqualTo(30))))
                .andExpect(header().exists("Retry-After"));
        assertThat(sms.sentTo(r.phone())).hasSize(1);
    }

    @Test
    void resendIssuesNewCodeAndOldCodeStopsWorking() throws Exception {
        Registered r = register("JOB_SEEKER");
        skipCooldown(r.userId());
        postJson("/api/auth/otp/resend", Map.of("phone", r.phone()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(ResendOtpResponse.MESSAGE))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(120))
                .andExpect(jsonPath("$.resendAvailableInSeconds").value(30));
        assertThat(sms.sentTo(r.phone())).hasSize(2);
        String newCode = sms.lastCodeFor(r.phone());
        assumeThat(newCode).isNotEqualTo(r.code());

        assertThat(queryInt("select resend_count from otp_code where user_id = ?", r.userId())).isEqualTo(1);
        assertThat(queryInt("select count(*) from otp_code where user_id = ?", r.userId())).isEqualTo(1);
        assertThat(auditActions(r.userId())).endsWith("OTP_RESENT");

        postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", r.code()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("OTP_INVALID"));
        verify(r.phone(), newCode);
    }

    @Test
    void resendLimitThreePerWindow() throws Exception {
        Registered r = register("JOB_SEEKER");
        for (int i = 1; i <= 3; i++) {
            skipCooldown(r.userId());
            var result = postJson("/api/auth/otp/resend", Map.of("phone", r.phone()))
                    .andExpect(status().isAccepted());
            if (i == 3) {
                // last allowed resend: next one only possible when the 30-minute window ends
                result.andExpect(jsonPath("$.resendAvailableInSeconds").value(greaterThan(1700)));
            }
        }
        assertThat(sms.sentTo(r.phone())).hasSize(4); // initial + 3 resends
        skipCooldown(r.userId());
        postJson("/api/auth/otp/resend", Map.of("phone", r.phone()))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("OTP_RESEND_LIMIT_REACHED"))
                .andExpect(jsonPath("$.retryAfterSeconds").value(allOf(greaterThan(1700), lessThanOrEqualTo(1800))))
                .andExpect(header().exists("Retry-After"));
        assertThat(sms.sentTo(r.phone())).hasSize(4);

        // after the control window a new one starts
        exec("update otp_code set created_at = systimestamp - interval '31' minute where user_id = ?", r.userId());
        postJson("/api/auth/otp/resend", Map.of("phone", r.phone())).andExpect(status().isAccepted());
        assertThat(sms.sentTo(r.phone())).hasSize(5);
        assertThat(queryInt("select count(*) from otp_code where user_id = ?", r.userId())).isEqualTo(2);
        verify(r.phone(), sms.lastCodeFor(r.phone()));
    }

    @Test
    void resendForUnknownPhoneIsGeneric202AndSendsNothing() throws Exception {
        postJson("/api/auth/otp/resend", Map.of("phone", newPhone()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(ResendOtpResponse.MESSAGE))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(120))
                .andExpect(jsonPath("$.resendAvailableInSeconds").value(30));
        assertThat(sms.all()).isEmpty();
    }

    @Test
    void resendForVerifiedPhoneIsGeneric202AndSendsNothing() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        sms.reset();
        postJson("/api/auth/otp/resend", Map.of("phone", a.phone()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(ResendOtpResponse.MESSAGE));
        assertThat(sms.all()).isEmpty();
    }

    @Test
    void resendValidation() throws Exception {
        postJson("/api/auth/otp/resend", Map.of("phone", "12"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("phone"));
    }

    /**
     * BUG: a resend whose SMS fails still consumes one of the 3 resends (resend_count is bumped and
     * committed because ApiException does not roll back), so a gateway outage can lock a user out of
     * verification for 30 minutes without them ever receiving a code.
     */
    @Test
    void failedResendDoesNotConsumeAResendSlot() throws Exception {
        Registered r = register("JOB_SEEKER");
        skipCooldown(r.userId());
        sms.setFailing(true);
        postJson("/api/auth/otp/resend", Map.of("phone", r.phone()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("SMS_DELIVERY_FAILED"));
        sms.setFailing(false);
        assertThat(queryInt("select resend_count from otp_code where user_id = ?", r.userId()))
                .as("resend_count after a failed delivery").isZero();
    }
}
