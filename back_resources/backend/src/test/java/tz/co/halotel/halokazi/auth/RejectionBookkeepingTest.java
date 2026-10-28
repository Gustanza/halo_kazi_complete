package tz.co.halotel.halokazi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

/**
 * AUTH-13: security bookkeeping written before an ApiException must commit. In production the
 * service method is the outermost transaction and commits on ApiException
 * (@BusinessTransaction = noRollbackFor ApiException). Here the services join the test transaction,
 * so the proof is that no participant marked that transaction rollback-only: had one done so,
 * a real (non-test) outer transaction would have rolled the bookkeeping back.
 */
class RejectionBookkeepingTest extends AbstractIntegrationTest {

    @Autowired
    private PlatformTransactionManager txManager;

    private void assertNotRollbackOnly() {
        TransactionStatus status = txManager.getTransaction(TransactionDefinition.withDefaults());
        assertThat(status.isNewTransaction()).isFalse();
        assertThat(status.isRollbackOnly()).as("shared transaction marked rollback-only").isFalse();
        txManager.commit(status); // participating: no-op
    }

    @Test
    void failedLoginKeepsCounterAttemptAndAudit() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        postJson("/api/auth/login", Map.of("identifier", a.email(), "password", "Wr0ng!pass"))
                .andExpect(status().isUnauthorized());
        assertNotRollbackOnly();
        assertThat(queryInt("select failed_login_count from app_user where user_id = ?", a.userId())).isEqualTo(1);
        assertThat(queryInt("select count(*) from login_attempt where user_id = ? and succeeded = 0", a.userId()))
                .isEqualTo(1);
    }

    @Test
    void lockoutAndUnknownUserKeepBookkeeping() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        for (int i = 0; i < 6; i++) {
            postJson("/api/auth/login", Map.of("identifier", a.email(), "password", "Wr0ng!pass"));
        }
        postJson("/api/auth/login", Map.of("identifier", newEmail(), "password", "Wr0ng!pass"))
                .andExpect(status().isUnauthorized());
        assertNotRollbackOnly();
    }

    @Test
    void wrongOtpKeepsAttemptCount() throws Exception {
        Registered r = register("JOB_SEEKER");
        postJson("/api/auth/otp/verify", Map.of("phone", r.phone(), "code", r.code().equals("000000") ? "111111" : "000000"))
                .andExpect(status().isBadRequest());
        assertNotRollbackOnly();
        assertThat(queryInt("select attempt_count from otp_code where user_id = ?", r.userId())).isEqualTo(1);
    }

    @Test
    void idleRevocationKeepsRevocation() throws Exception {
        Active a = registerAndVerify("JOB_SEEKER");
        exec("update user_session set last_activity_at = systimestamp - interval '11' minute where user_id = ?",
                a.userId());
        me(a.token()).andExpect(status().isUnauthorized());
        assertNotRollbackOnly();
    }

    @Test
    void smsFailureKeepsSmsLog() throws Exception {
        sms.setFailing(true);
        postJson("/api/auth/register", registerBody("JOB_SEEKER", newPhone(), newEmail()))
                .andExpect(status().isBadGateway());
        assertNotRollbackOnly();
    }
}
