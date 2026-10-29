package tz.co.halotel.halokazi.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import tools.jackson.databind.json.JsonMapper;
import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

class AccountSettingsTest extends AbstractIntegrationTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final String NEW_PASSWORD = "N3w!Passw0rd";

    private ResultActions getSettings(String token) throws Exception {
        return mvc.perform(get("/api/me/settings").header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    private ResultActions putJson(String url, String token, Object body) throws Exception {
        return mvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(JSON.writeValueAsString(body)));
    }

    private ResultActions changePassword(String token, String current, String next, String confirm) throws Exception {
        return mvc.perform(post("/api/me/password").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsString(Map.of("currentPassword", current, "newPassword", next,
                        "confirmPassword", confirm))));
    }

    // ------------------------------------------------------------------ read

    @Test
    void newJobSeekerSeesTheirAccountAndDefaults() throws Exception {
        Active me = registerAndVerify("job_seeker");
        getSettings(me.token()).andExpect(status().isOk())
                .andExpect(jsonPath("$.account.fullName").value("Test Person"))
                .andExpect(jsonPath("$.account.email").value(me.email()))
                .andExpect(jsonPath("$.account.phone").value(me.phone()))
                .andExpect(jsonPath("$.account.dateOfBirth").doesNotExist())
                .andExpect(jsonPath("$.account.gender").doesNotExist())
                .andExpect(jsonPath("$.account.location.region").doesNotExist())
                .andExpect(jsonPath("$.notifications.email").value(true))
                .andExpect(jsonPath("$.notifications.applicationUpdates").value(true))
                .andExpect(jsonPath("$.notifications.jobAlerts").value(true))
                .andExpect(jsonPath("$.notifications.newsletter").value(false))
                .andExpect(jsonPath("$.privacy.profileVisible").value(true))
                .andExpect(jsonPath("$.privacy.showEmail").value(false))
                .andExpect(jsonPath("$.privacy.showPhone").value(false))
                .andExpect(jsonPath("$.preferences.language").value("EN"));
    }

    @Test
    void profileDetailsAppearOnceFilledIn() throws Exception {
        Active me = registerAndVerify("job_seeker");
        exec("""
                update job_seeker_profile set date_of_birth = date '2001-01-12', gender = 'MALE',
                       professional_summary = 'Marketing professional',
                       region_id = (select region_id from region where code = 'DAR_ES_SALAAM'),
                       address_line = 'Mikocheni'
                 where user_id = ?""", me.userId());
        getSettings(me.token())
                .andExpect(jsonPath("$.account.dateOfBirth").value("2001-01-12"))
                .andExpect(jsonPath("$.account.gender").value("MALE"))
                .andExpect(jsonPath("$.account.about").value("Marketing professional"))
                .andExpect(jsonPath("$.account.location.region.code").value("DAR_ES_SALAAM"))
                .andExpect(jsonPath("$.account.location.region.nameEn").value("Dar es Salaam"))
                .andExpect(jsonPath("$.account.location.address").value("Mikocheni"));
    }

    @Test
    void requiresALoggedInJobSeeker() throws Exception {
        mvc.perform(get("/api/me/settings")).andExpect(status().isUnauthorized());
        Active employer = registerAndVerify("employer");
        getSettings(employer.token()).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("JOB_SEEKERS_ONLY"));
        putJson("/api/me/settings/language", employer.token(), Map.of("language", "SW"))
                .andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------------ updates

    @Test
    void savesNotificationChoices() throws Exception {
        Active me = registerAndVerify("job_seeker");
        putJson("/api/me/settings/notifications", me.token(),
                Map.of("email", false, "applicationUpdates", true, "jobAlerts", false, "newsletter", true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications.email").value(false))
                .andExpect(jsonPath("$.notifications.jobAlerts").value(false))
                .andExpect(jsonPath("$.notifications.newsletter").value(true));
        assertThat(queryInt("select receive_job_alerts from job_seeker_profile where user_id = ?", me.userId()))
                .isZero();
        assertThat(queryInt("select notify_newsletter from job_seeker_profile where user_id = ?", me.userId()))
                .isOne();
        getSettings(me.token()).andExpect(jsonPath("$.notifications.email").value(false));
        assertThat(auditActions(me.userId())).contains("SETTINGS_UPDATED");
    }

    @Test
    void notificationFieldsAreAllRequired() throws Exception {
        Active me = registerAndVerify("job_seeker");
        putJson("/api/me/settings/notifications", me.token(), Map.of("email", false))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(3));
    }

    @Test
    void savesPrivacyChoices() throws Exception {
        Active me = registerAndVerify("job_seeker");
        putJson("/api/me/settings/privacy", me.token(),
                Map.of("profileVisible", false, "showEmail", true, "showPhone", true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privacy.profileVisible").value(false))
                .andExpect(jsonPath("$.privacy.showEmail").value(true))
                .andExpect(jsonPath("$.privacy.showPhone").value(true));
        assertThat(queryInt("select profile_visible from job_seeker_profile where user_id = ?", me.userId())).isZero();
    }

    @Test
    void savesLanguage() throws Exception {
        Active me = registerAndVerify("job_seeker");
        putJson("/api/me/settings/language", me.token(), Map.of("language", "sw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.language").value("SW"));
        me(me.token()).andExpect(jsonPath("$.preferredLanguage").value("SW"));
        putJson("/api/me/settings/language", me.token(), Map.of("language", "FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("language"));
    }

    // ------------------------------------------------------------------ password

    @Test
    void changesPasswordAndSignsOutOtherDevices() throws Exception {
        Active me = registerAndVerify("job_seeker");
        String otherDevice = login(me.phone(), PASSWORD);

        changePassword(me.token(), PASSWORD, NEW_PASSWORD, NEW_PASSWORD).andExpect(status().isNoContent());

        me(me.token()).andExpect(status().isOk()); // the device that made the change stays signed in
        me(otherDevice).andExpect(status().isUnauthorized());
        assertThat(queryString("select revoke_reason from user_session where user_id = ? and revoked_at is not null",
                me.userId())).isEqualTo("PASSWORD_CHANGED");
        postJson("/api/auth/login", Map.of("identifier", me.phone(), "password", PASSWORD))
                .andExpect(status().isUnauthorized());
        login(me.phone(), NEW_PASSWORD);
        assertThat(auditActions(me.userId())).contains("PASSWORD_CHANGED");
    }

    @Test
    void wrongCurrentPasswordIsRejectedAndAudited() throws Exception {
        Active me = registerAndVerify("job_seeker");
        changePassword(me.token(), "Wr0ng!Password", NEW_PASSWORD, NEW_PASSWORD)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CURRENT_PASSWORD_INCORRECT"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("currentPassword"));
        assertThat(auditActions(me.userId())).contains("PASSWORD_CHANGE_FAILED");
        me(me.token()).andExpect(status().isOk()); // a 400, not a 401: the user stays signed in
    }

    @Test
    void newPasswordRules() throws Exception {
        Active me = registerAndVerify("job_seeker");
        changePassword(me.token(), PASSWORD, NEW_PASSWORD, "Different!1A")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("confirmPassword"));
        changePassword(me.token(), PASSWORD, "weakpass", "weakpass")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("newPassword"));
        changePassword(me.token(), PASSWORD, PASSWORD, PASSWORD)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("newPassword"));
        mvc.perform(post("/api/me/password").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
