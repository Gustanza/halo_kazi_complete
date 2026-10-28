package tz.co.halotel.halokazi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

/** AUTH-04 (email or phone login) and AUTH-13 (lockout, bookkeeping). */
class LoginTest extends AbstractIntegrationTest {

    private static Map<String, String> creds(String identifier, String password) {
        return Map.of("identifier", identifier, "password", password);
    }

    private int failedCount(long userId) {
        return queryInt("select failed_login_count from app_user where user_id = ?", userId);
    }

    @Test
    void loginByPhoneInEveryFormat() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        String sub = a.phone().substring(4); // 9 digits
        List<String> formats = List.of(
                sub.substring(0, 3) + " " + sub.substring(3, 6) + " " + sub.substring(6),
                sub,
                "0" + sub,
                "0" + sub.substring(0, 3) + "-" + sub.substring(3, 6) + "-" + sub.substring(6),
                "255" + sub,
                "+255" + sub,
                "+255 " + sub.substring(0, 3) + " " + sub.substring(3, 6) + " " + sub.substring(6));
        for (String identifier : formats) {
            postJson("/api/auth/login", creds(identifier, PASSWORD))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.user.userId").value(a.userId()))
                    .andExpect(jsonPath("$.idleTimeoutSeconds").value(600));
        }
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and succeeded = 1", a.userId()))
                .isEqualTo(formats.size());
        // identifier stored as typed (trimmed)
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and identifier = ?",
                a.userId(), formats.getFirst())).isEqualTo(1);
    }

    @Test
    void loginByEmailIsCaseInsensitiveAndTrimmed() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        String mixed = a.email().substring(0, 4).toUpperCase() + a.email().substring(4, 10)
                + a.email().substring(10).toUpperCase();
        login(mixed, PASSWORD);
        login("  " + a.email().toUpperCase() + "  ", PASSWORD);
    }

    @Test
    void eachLoginOpensANewSession() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        String t1 = login(a.email(), PASSWORD);
        String t2 = login(a.phone(), PASSWORD);
        assertThat(t1).isNotEqualTo(t2).isNotEqualTo(a.token());
        assertThat(queryInt("select count(*) from user_session where user_id = ? and revoked_at is null", a.userId()))
                .isEqualTo(3);
        me(t1).andExpect(status().isOk());
        me(t2).andExpect(status().isOk());
    }

    @Test
    void wrongPasswordIs401AndCounted() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        postJson("/api/auth/login", creds(a.email(), PASSWORD + "x"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_CREDENTIALS.defaultMessage()));
        assertThat(failedCount(a.userId())).isEqualTo(1);
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and succeeded = 0 "
                + "and failure_reason = 'BAD_PASSWORD'", a.userId())).isEqualTo(1);
        assertThat(queryInt("select count(*) from audit_log where user_id = ? and action = 'LOGIN_FAILED' "
                + "and result = 'FAILURE'", a.userId())).isEqualTo(1);

        // a later success resets the counter
        login(a.email(), PASSWORD);
        assertThat(failedCount(a.userId())).isZero();
    }

    @Test
    void fiveFailuresLockTheAccountFor15Minutes() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        for (int i = 1; i <= 4; i++) {
            postJson("/api/auth/login", creds(a.phone(), "Wr0ng!pass" + i))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
            assertThat(failedCount(a.userId())).isEqualTo(i);
        }
        postJson("/api/auth/login", creds(a.phone(), "Wr0ng!pass5"))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.code").value("ACCOUNT_LOCKED"))
                .andExpect(jsonPath("$.retryAfterSeconds").value(allOf(greaterThan(890), lessThanOrEqualTo(900))))
                .andExpect(header().exists("Retry-After"));
        assertThat(queryInt("select count(*) from app_user where user_id = ? and locked_until between "
                + "systimestamp + interval '14' minute and systimestamp + interval '15' minute + interval '5' second",
                a.userId())).isEqualTo(1);

        // the right password does not get in while locked
        postJson("/api/auth/login", creds(a.email(), PASSWORD))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.code").value("ACCOUNT_LOCKED"));
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and failure_reason = 'ACCOUNT_LOCKED'",
                a.userId())).isEqualTo(1);
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and succeeded = 0", a.userId()))
                .isEqualTo(6);
        // existing sessions keep working (lockout only blocks new logins)
        me(a.token()).andExpect(status().isOk());
    }

    @Test
    void lockExpiryLetsTheUserBackInAndResetsTheCounter() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        for (int i = 0; i < 5; i++) {
            postJson("/api/auth/login", creds(a.email(), "Wr0ng!pass"));
        }
        assertThat(queryInt("select count(*) from app_user where user_id = ? and locked_until > systimestamp",
                a.userId())).isEqualTo(1);
        exec("update app_user set locked_until = systimestamp - interval '1' second where user_id = ?", a.userId());

        login(a.email(), PASSWORD);
        assertThat(failedCount(a.userId())).isZero();
        assertThat(queryInt("select count(*) from app_user where user_id = ? and locked_until is null", a.userId()))
                .isEqualTo(1);
    }

    @Test
    void afterLockExpiryAFreshFiveAttemptsAreAllowed() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        for (int i = 0; i < 5; i++) {
            postJson("/api/auth/login", creds(a.email(), "Wr0ng!pass"));
        }
        exec("update app_user set locked_until = systimestamp - interval '1' second where user_id = ?", a.userId());
        postJson("/api/auth/login", creds(a.email(), "Wr0ng!pass"))
                .andExpect(status().isUnauthorized());
        assertThat(failedCount(a.userId())).isEqualTo(1);
    }

    @Test
    void pendingUserWithCorrectPasswordGets403PhoneNotVerified() throws Exception {
        Registered r = register("JOB_SEEKER");
        postJson("/api/auth/login", creds(r.email(), PASSWORD))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("PHONE_NOT_VERIFIED"))
                .andExpect(jsonPath("$.details.phone").value(r.phone()))
                .andExpect(jsonPath("$.token").doesNotExist());
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and failure_reason = 'PHONE_NOT_VERIFIED'",
                r.userId())).isEqualTo(1);
        assertThat(queryInt("select count(*) from user_session where user_id = ?", r.userId())).isZero();
    }

    @Test
    void pendingUserWithWrongPasswordGets401NotStatus() throws Exception {
        Registered r = register("JOB_SEEKER");
        postJson("/api/auth/login", creds(r.email(), "Wr0ng!pass"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void suspendedUserGets403AccountDisabled() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        exec("update app_user set account_status = 'SUSPENDED' where user_id = ?", a.userId());
        postJson("/api/auth/login", creds(a.email(), PASSWORD))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCOUNT_DISABLED"));
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and failure_reason = 'ACCOUNT_DISABLED'",
                a.userId())).isEqualTo(1);
        // and the existing session is refused too
        me(a.token()).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCOUNT_DISABLED"));
    }

    @Test
    void unknownUserGetsSameAnswerAsWrongPassword() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        MvcResult wrongPw = postJson("/api/auth/login", creds(a.email(), "Wr0ng!pass"))
                .andExpect(status().isUnauthorized()).andReturn();
        for (String unknown : List.of(newEmail(), newPhone(), "not-a-phone")) {
            MvcResult r = postJson("/api/auth/login", creds(unknown, "Wr0ng!pass"))
                    .andExpect(status().isUnauthorized()).andReturn();
            assertThat(body(r)).isEqualTo(body(wrongPw));
            assertThat(queryInt("select count(*) from login_attempt where identifier = ? and user_id is null "
                    + "and failure_reason = 'UNKNOWN_IDENTIFIER'", unknown)).isEqualTo(1);
        }
    }

    @Test
    void loginValidation() throws Exception {
        postJson("/api/auth/login", Map.of("identifier", " ", "password", "x"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("identifier"));
        postJson("/api/auth/login", Map.of("identifier", "a@b.tz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("password"));
    }

    @Test
    void successfulLoginRecordsBookkeeping() throws Exception {
        Active a = registerAndVerify("EMPLOYER");
        MvcResult r = postJson("/api/auth/login", creds(a.email(), PASSWORD)).andExpect(status().isOk())
                .andExpect(jsonPath("$.user.employerVerificationStatus").value("NOT_SUBMITTED"))
                .andReturn();
        String token = JsonPath.read(body(r), "$.token");
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and succeeded = 1 "
                + "and failure_reason is null", a.userId())).isEqualTo(1);
        assertThat(auditActions(a.userId())).endsWith("LOGIN_SUCCESS");
        assertThat(queryInt("select count(*) from user_session where token_hash = ?", token)).isZero();
    }
}
