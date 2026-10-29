package tz.co.halotel.halokazi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import tz.co.halotel.halokazi.common.security.TokenHasher;
import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

/** AUTH-01, AUTH-02, AUTH-06, AUTH-07: registration, phone verification, validation, duplicates. */
class RegistrationFlowTest extends AbstractIntegrationTest {

    @Test
    void jobSeekerFullFlow_register_verify_me_logout() throws Exception {
        String phone = newPhone();
        String subscriber = phone.substring(4);
        String email = newEmail();
        Map<String, Object> body = registerBody("JOB_SEEKER", "0" + subscriber, email.toUpperCase());
        body.put("fullName", "  Asha Mohamed  ");

        // ---- register
        MvcResult reg = postJson("/api/auth/register", body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.role").value("JOB_SEEKER"))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value(300))
                .andExpect(jsonPath("$.resendAvailableInSeconds").value(30))
                .andReturn();
        long userId = ((Number) JsonPath.read(body(reg), "$.userId")).longValue();

        assertThat(sms.sentTo(phone)).hasSize(1);
        String code = sms.lastCodeFor(phone);
        assertThat(sms.sentTo(phone).getFirst().text()).contains("expires in 5 minutes");

        assertThat(queryString("select account_status from app_user where user_id = ?", userId))
                .isEqualTo("PENDING_VERIFICATION");
        assertThat(queryInt("select phone_verified from app_user where user_id = ?", userId)).isZero();
        assertThat(queryString("select email from app_user where user_id = ?", userId)).isEqualTo(email);
        assertThat(queryString("select full_name from app_user where user_id = ?", userId)).isEqualTo("Asha Mohamed");
        assertThat(queryString("select phone from app_user where user_id = ?", userId)).isEqualTo(phone);
        assertThat(queryString("select role from app_user where user_id = ?", userId)).isEqualTo("JOB_SEEKER");
        assertThat(queryInt("select count(*) from job_seeker_profile where user_id = ?", userId)).isEqualTo(1);
        assertThat(queryInt("select count(*) from employer where user_id = ?", userId)).isZero();
        assertThat(queryInt("select count(*) from otp_code where user_id = ? and consumed_at is null "
                + "and purpose = 'PHONE_VERIFICATION' and resend_count = 0 and attempt_count = 0", userId)).isEqualTo(1);
        // expires_at is ~300 s from now
        assertThat(queryInt("select count(*) from otp_code where user_id = ? and expires_at between "
                + "systimestamp + interval '280' second and systimestamp + interval '305' second", userId)).isEqualTo(1);
        assertThat(queryInt("select count(*) from sms_log where user_id = ? and delivery_status = 'SENT' "
                + "and purpose = 'PHONE_VERIFICATION' and provider_message_id like 'test-%' and sent_at is not null",
                userId)).isEqualTo(1);
        assertThat(auditActions(userId)).containsExactly("USER_REGISTERED", "OTP_SENT");

        // secrets are hashed
        String passwordHash = queryString("select password_hash from app_user where user_id = ?", userId);
        assertThat(passwordHash).startsWith("$2").isNotEqualTo(PASSWORD).doesNotContain(PASSWORD);
        String codeHash = queryString("select code_hash from otp_code where user_id = ?", userId);
        assertThat(codeHash).startsWith("$2").doesNotContain(code);

        // ---- me before verification needs a token
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));

        // ---- verify (phone given in yet another format)
        MvcResult verified = postJson("/api/auth/otp/verify", Map.of("phone", subscriber, "code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.idleTimeoutSeconds").value(600))
                .andExpect(jsonPath("$.user.userId").value(userId))
                .andExpect(jsonPath("$.user.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.user.role").value("JOB_SEEKER"))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.employerVerificationStatus").doesNotExist())
                .andReturn();
        String token = JsonPath.read(body(verified), "$.token");
        OffsetDateTime expiresAt = OffsetDateTime.parse(JsonPath.read(body(verified), "$.expiresAt"));
        assertThat(expiresAt).isBetween(OffsetDateTime.now().plusHours(11).plusMinutes(59),
                OffsetDateTime.now().plusHours(12).plusMinutes(1));

        assertThat(queryString("select account_status from app_user where user_id = ?", userId)).isEqualTo("ACTIVE");
        assertThat(queryInt("select phone_verified from app_user where user_id = ?", userId)).isEqualTo(1);
        assertThat(queryInt("select count(*) from app_user where user_id = ? and phone_verified_at is not null "
                + "and last_login_at is not null", userId)).isEqualTo(1);
        assertThat(queryInt("select count(*) from otp_code where user_id = ? and consumed_at is not null", userId))
                .isEqualTo(1);
        assertThat(queryInt("select count(*) from user_session where user_id = ? and revoked_at is null", userId))
                .isEqualTo(1);
        assertThat(queryString("select token_hash from user_session where user_id = ?", userId))
                .isEqualTo(TokenHasher.sha256Hex(token)).isNotEqualTo(token);
        assertThat(auditActions(userId)).containsExactly("USER_REGISTERED", "OTP_SENT", "PHONE_VERIFIED");

        // ---- me
        me(token).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.fullName").value("Asha Mohamed"))
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.preferredLanguage").value("EN"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));

        // ---- logout
        logout(token).andExpect(status().isNoContent());
        assertThat(queryString("select revoke_reason from user_session where user_id = ?", userId)).isEqualTo("LOGOUT");
        assertThat(auditActions(userId)).endsWith("LOGOUT");

        me(token).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
        logout(token).andExpect(status().isUnauthorized());

        // ---- login afterwards writes login_attempt + LOGIN_SUCCESS
        login(email, PASSWORD);
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and succeeded = 1", userId))
                .isEqualTo(1);
        assertThat(auditActions(userId)).endsWith("LOGIN_SUCCESS");

        // no secret ever reached the audit log
        assertThat(queryInt("select count(*) from audit_log where user_id = ? and (dbms_lob.instr(details, ?) > 0 "
                + "or dbms_lob.instr(details, ?) > 0 or dbms_lob.instr(details, ?) > 0)",
                userId, code, PASSWORD, token)).isZero();
        assertThat(queryInt("select count(*) from user_session where token_hash = ?", token)).isZero();
    }

    @Test
    void employerRegistersWithLowercaseRole() throws Exception {
        String phone = newPhone();
        String email = newEmail();
        Map<String, Object> body = registerBody("employer", phone, email.toUpperCase());
        body.put("fullName", "Kilimanjaro Coffee Ltd");
        MvcResult result = postJson("/api/auth/register", body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("EMPLOYER"))
                .andReturn();
        long userId = ((Number) JsonPath.read(body(result), "$.userId")).longValue();

        assertThat(queryString("select role from app_user where user_id = ?", userId)).isEqualTo("EMPLOYER");
        assertThat(queryString("select business_name from employer where user_id = ?", userId))
                .isEqualTo("Kilimanjaro Coffee Ltd");
        assertThat(queryString("select company_email from employer where user_id = ?", userId)).isEqualTo(email);
        assertThat(queryString("select verification_status from employer where user_id = ?", userId))
                .isEqualTo("NOT_SUBMITTED");
        assertThat(queryInt("select count(*) from job_seeker_profile where user_id = ?", userId)).isZero();

        String token = verify(phone, sms.lastCodeFor(phone));
        me(token).andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("EMPLOYER"))
                .andExpect(jsonPath("$.employerVerificationStatus").value("NOT_SUBMITTED"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"job_seeker", "Job_Seeker", " JOB_SEEKER "})
    void roleIsCaseInsensitive(String role) throws Exception {
        postJson("/api/auth/register", registerBody(role, newPhone(), newEmail()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("JOB_SEEKER"));
    }

    // ------------------------------------------------------------------ validation

    private void expectFieldError(Map<String, Object> body, String field) throws Exception {
        postJson("/api/auth/register", body)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem(field)));
        assertThat(sms.all()).as("no SMS on invalid input").isEmpty();
    }

    @Test
    void validation_eachRequiredFieldMissing() throws Exception {
        for (String field : new String[] {"role", "fullName", "phone", "email", "password", "repeatPassword"}) {
            Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
            body.remove(field);
            expectFieldError(body, field);
        }
    }

    @Test
    void validation_blankFields() throws Exception {
        for (String field : new String[] {"fullName", "phone", "email", "password", "repeatPassword"}) {
            Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
            body.put(field, "   ");
            expectFieldError(body, field);
        }
    }

    @Test
    void validation_passwordMismatch() throws Exception {
        Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
        body.put("repeatPassword", PASSWORD + "x");
        postJson("/api/auth/register", body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("repeatPassword"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Passwords do not match."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcde1!", "str0ng!pass", "STR0NG!PASS", "Strong!Pass", "Str0ngPass1"})
    void validation_weakPasswords(String weak) throws Exception {
        Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
        body.put("password", weak);
        body.put("repeatPassword", weak);
        postJson("/api/auth/register", body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password')].message",
                        hasItem(org.hamcrest.Matchers.containsString("at least 8 characters"))));
    }

    @Test
    void validation_passwordLongerThan64() throws Exception {
        String longPw = "Aa1!" + "x".repeat(61); // 65
        Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
        body.put("password", longPw);
        body.put("repeatPassword", longPw);
        expectFieldError(body, "password");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "812345678", "+254712345678", "07123456789", "phone"})
    void validation_badPhone(String phone) throws Exception {
        expectFieldError(registerBody("JOB_SEEKER", phone, newEmail()), "phone");
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-an-email", "a@", "@b.tz", "a b@c.tz"})
    void validation_badEmail(String email) throws Exception {
        expectFieldError(registerBody("JOB_SEEKER", newPhone(), email), "email");
    }

    @Test
    void validation_tooLongFullName() throws Exception {
        Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
        body.put("fullName", "x".repeat(151));
        expectFieldError(body, "fullName");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPER_ADMIN", "recruiter", ""})
    void validation_unknownOrPrivilegedRole(String role) throws Exception {
        expectFieldError(registerBody(role, newPhone(), newEmail()), "role");
    }

    @Test
    void malformedJsonIs400() throws Exception {
        postJson("/api/auth/register", "{not json").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    void nonJsonBodyIs415() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(org.springframework.http.MediaType.TEXT_PLAIN).content("{}"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @Test
    void validationReportsAllFieldsAtOnce() throws Exception {
        postJson("/api/auth/register", "{}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(6)))
                .andExpect(jsonPath("$.fieldErrors[*].field", everyItem(not(nullValue()))));
    }

    // ------------------------------------------------------------------ duplicates

    @Test
    void duplicatePhone_pendingVerification() throws Exception {
        Registered first = register("JOB_SEEKER");
        String subscriber = first.phone().substring(4);
        postJson("/api/auth/register", registerBody("EMPLOYER", "0" + subscriber, newEmail()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PHONE_PENDING_VERIFICATION"))
                .andExpect(jsonPath("$.details.phone").value(first.phone()))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("phone"));
        assertThat(sms.sentTo(first.phone())).hasSize(1);
    }

    @Test
    void duplicatePhone_verified() throws Exception {
        Active first = registerAndVerify("JOB_SEEKER");
        postJson("/api/auth/register", registerBody("JOB_SEEKER", first.phone(), newEmail()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PHONE_ALREADY_REGISTERED"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    @Test
    void duplicateEmail_isCaseInsensitive() throws Exception {
        Registered first = register("JOB_SEEKER");
        postJson("/api/auth/register", registerBody("JOB_SEEKER", newPhone(), first.email().toUpperCase()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_REGISTERED"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
    }

    @Test
    void duplicateChecksDoNotCreateRows() throws Exception {
        Registered first = register("JOB_SEEKER");
        int users = queryInt("select count(*) from app_user");
        postJson("/api/auth/register", registerBody("JOB_SEEKER", newPhone(), first.email()))
                .andExpect(status().isConflict());
        assertThat(queryInt("select count(*) from app_user")).isEqualTo(users);
    }

    // ------------------------------------------------------------------ SMS outage

    @Test
    void smsOutage_returns502_andLogsFailure() throws Exception {
        sms.setFailing(true);
        String phone = newPhone();
        postJson("/api/auth/register", registerBody("JOB_SEEKER", phone, newEmail()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("SMS_DELIVERY_FAILED"));
        // The account is kept (ApiException commits), so the user can ask for a new code.
        assertThat(queryString("select account_status from app_user where phone = ?", phone))
                .isEqualTo("PENDING_VERIFICATION");
        assertThat(queryInt("select count(*) from sms_log s join app_user u on u.user_id = s.user_id "
                + "where u.phone = ? and s.delivery_status = 'FAILED' and s.error_message is not null", phone))
                .isEqualTo(1);
    }

    /**
     * BUG: the SMS failed, the 502 message says "Please request a new code", but the OTP row was
     * already saved before delivery so the 30 s resend cooldown applies to a code that never left.
     */
    @Test
    void smsOutage_userCanImmediatelyRequestANewCode() throws Exception {
        sms.setFailing(true);
        String phone = newPhone();
        postJson("/api/auth/register", registerBody("JOB_SEEKER", phone, newEmail()))
                .andExpect(status().isBadGateway());
        sms.setFailing(false);

        postJson("/api/auth/otp/resend", Map.of("phone", phone)).andExpect(status().isAccepted());
        assertThat(sms.sentTo(phone)).hasSize(1);
    }

    @Test
    void responsesNeverEchoPassword() throws Exception {
        Map<String, Object> body = registerBody("JOB_SEEKER", newPhone(), newEmail());
        MvcResult r = postJson("/api/auth/register", body).andExpect(status().isCreated()).andReturn();
        assertThat(body(r)).doesNotContain(PASSWORD).doesNotContain("password");
    }
}
