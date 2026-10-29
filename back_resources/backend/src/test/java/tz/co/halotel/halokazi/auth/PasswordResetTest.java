package tz.co.halotel.halokazi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

class PasswordResetTest extends AbstractIntegrationTest {

    private static final String NEW_PASSWORD = "R3set!Passw0rd";

    private ResultActions forgot(String identifier) throws Exception {
        return postJson("/api/auth/password/forgot", Map.of("identifier", identifier));
    }

    private ResultActions reset(String identifier, String code, String next, String confirm) throws Exception {
        return postJson("/api/auth/password/reset",
                Map.of("identifier", identifier, "code", code, "newPassword", next, "confirmPassword", confirm));
    }

    @Test
    void resetByPhoneSignsOutEverywhereAndAllowsLoginWithNewPassword() throws Exception {
        Active me = registerAndVerify("job_seeker");
        sms.reset();

        forgot(me.phone()).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(300));
        String code = sms.lastCodeFor(me.phone());
        assertThat(queryInt("select count(*) from otp_code where user_id = ? and purpose = 'PASSWORD_RESET'",
                me.userId())).isOne();

        reset(me.phone(), code, NEW_PASSWORD, NEW_PASSWORD).andExpect(status().isNoContent());

        me(me.token()).andExpect(status().isUnauthorized()); // every session ended
        postJson("/api/auth/login", Map.of("identifier", me.phone(), "password", PASSWORD))
                .andExpect(status().isUnauthorized());
        login(me.phone(), NEW_PASSWORD);
        assertThat(auditActions(me.userId())).contains("PASSWORD_RESET_REQUESTED", "PASSWORD_RESET");
    }

    @Test
    void resetByEmailSendsTheCodeToThePhone() throws Exception {
        Active me = registerAndVerify("job_seeker");
        sms.reset();
        forgot(me.email().toUpperCase()).andExpect(status().isAccepted());
        String code = sms.lastCodeFor(me.phone());
        reset(me.email(), code, NEW_PASSWORD, NEW_PASSWORD).andExpect(status().isNoContent());
        login(me.email(), NEW_PASSWORD);
    }

    @Test
    void resetLiftsALoginLock() throws Exception {
        Active me = registerAndVerify("job_seeker");
        exec("update app_user set locked_until = systimestamp + interval '10' minute, failed_login_count = 3"
                + " where user_id = ?", me.userId());
        sms.reset();
        forgot(me.phone());
        reset(me.phone(), sms.lastCodeFor(me.phone()), NEW_PASSWORD, NEW_PASSWORD).andExpect(status().isNoContent());
        login(me.phone(), NEW_PASSWORD);
    }

    @Test
    void unknownAndUnverifiedAccountsGetTheSameAnswerAndNoSms() throws Exception {
        sms.reset();
        String unknown = newPhone();
        forgot(unknown).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(PasswordResetService.SENT_MESSAGE));
        Registered pending = register("job_seeker");
        sms.reset();
        forgot(pending.phone()).andExpect(status().isAccepted());
        assertThat(sms.sentTo(unknown)).isEmpty();
        assertThat(sms.sentTo(pending.phone())).isEmpty();
        reset(unknown, "123456", NEW_PASSWORD, NEW_PASSWORD).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("OTP_INVALID"));
    }

    @Test
    void wrongCodeAndRules() throws Exception {
        Active me = registerAndVerify("job_seeker");
        sms.reset();
        forgot(me.phone());
        String code = sms.lastCodeFor(me.phone());
        String wrong = code.equals("000000") ? "111111" : "000000";

        reset(me.phone(), wrong, NEW_PASSWORD, NEW_PASSWORD).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("OTP_INVALID"))
                .andExpect(jsonPath("$.details.remainingAttempts").value(4));
        reset(me.phone(), code, NEW_PASSWORD, "Other!Passw0rd").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("confirmPassword"));
        reset(me.phone(), code, "weakpass", "weakpass").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("newPassword"));
        login(me.phone(), PASSWORD); // nothing was reset: the old password still works
    }

    @Test
    void resendIsRateLimited() throws Exception {
        Active me = registerAndVerify("job_seeker");
        forgot(me.phone()).andExpect(status().isAccepted());
        forgot(me.phone()).andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("OTP_RESEND_TOO_SOON"))
                .andExpect(header().exists("Retry-After"));
    }
}
