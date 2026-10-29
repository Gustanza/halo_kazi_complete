package tz.co.halotel.halokazi.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

/** AUTH-12: bearer sessions, idle timeout, absolute expiry, revocation, public routes. */
class SessionTest extends AbstractIntegrationTest {

    @Autowired
    private Clock clock;

    @Test
    void missingOrMalformedBearerIs401() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
        for (String header : new String[] {"Bearer", "Bearer ", "Bearer    ", "Basic dXNlcjpwYXNz", "garbage",
                "Bearer not-a-real-token"}) {
            mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, header))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
        }
        mvc.perform(post("/api/auth/logout")).andExpect(status().isUnauthorized());
    }

    @Test
    void bearerSchemeIsCaseInsensitive() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, "bearer " + a.token()))
                .andExpect(status().isOk());
    }

    @Test
    void idleTimeoutExpiresAndRevokesTheSession() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        exec("update user_session set last_activity_at = systimestamp - interval '11' minute where user_id = ?",
                a.userId());
        me(a.token()).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));
        assertThat(queryString("select revoke_reason from user_session where user_id = ?", a.userId()))
                .isEqualTo("IDLE_TIMEOUT");
        assertThat(queryInt("select count(*) from user_session where user_id = ? and revoked_at is not null",
                a.userId())).isEqualTo(1);
        // once revoked it's just an unknown session
        me(a.token()).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    void exactlyTenMinutesIdleIsExpired() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        // The app's clock, not the database's: the shared DB runs on another machine whose clock can differ
        // by a fraction of a second, which matters exactly at the boundary.
        exec("update user_session set last_activity_at = ? where user_id = ?",
                OffsetDateTime.now(clock).minusMinutes(10), a.userId());
        me(a.token()).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));
    }

    @Test
    void activityWithinTenMinutesSlidesTheWindow() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        exec("update user_session set last_activity_at = systimestamp - interval '9' minute where user_id = ?",
                a.userId());
        me(a.token()).andExpect(status().isOk());
        assertThat(queryInt("select count(*) from user_session where user_id = ? "
                + "and last_activity_at > systimestamp - interval '10' second", a.userId())).isEqualTo(1);
    }

    @Test
    void absoluteExpiryIsEnforced() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        exec("update user_session set expires_at = systimestamp - interval '1' second where user_id = ?", a.userId());
        me(a.token()).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));
    }

    @Test
    void revokedSessionIsRejected() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        exec("update user_session set revoked_at = systimestamp, revoke_reason = 'ADMIN_REVOKED' where user_id = ?",
                a.userId());
        me(a.token()).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    void logoutOnlyRevokesTheCurrentSession() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        String second = login(a.email(), PASSWORD);
        logout(a.token()).andExpect(status().isNoContent());
        me(a.token()).andExpect(status().isUnauthorized());
        me(second).andExpect(status().isOk());
    }

    @Test
    void publicEndpointsDoNotRequireAToken() throws Exception {
        postJson("/api/auth/login", Map.of("identifier", newEmail(), "password", "x"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
        postJson("/api/auth/register", "{}")
                .andExpect(status().isBadRequest());
        postJson("/api/auth/otp/resend", Map.of("phone", newPhone()))
                .andExpect(status().isAccepted());
        postJson("/api/auth/otp/verify", Map.of("phone", newPhone(), "code", "123456"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("OTP_INVALID"));
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void publicEndpointsIgnoreAGarbageToken() throws Exception {
        mvc.perform(post("/api/auth/otp/resend").contentType("application/json")
                        .content("{\"phone\":\"" + newPhone() + "\"}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer garbage"))
                .andExpect(status().isAccepted());
    }

    @Test
    void unknownApiRouteIs404Not401() throws Exception {
        mvc.perform(get("/api/does-not-exist")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void wrongMethodIs405() throws Exception {
        mvc.perform(get("/api/auth/login")).andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }
}
