package tz.co.halotel.halokazi.vacancy;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

class SavedJobsTest extends AbstractIntegrationTest {

    private ResultActions ids(String token) throws Exception {
        return mvc.perform(get("/api/me/saved-jobs/ids").header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    private ResultActions save(String token, long id) throws Exception {
        return mvc.perform(put("/api/me/saved-jobs/" + id).header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    private ResultActions unsave(String token, long id) throws Exception {
        return mvc.perform(delete("/api/me/saved-jobs/" + id).header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    @Test
    void saveListAndRemove() throws Exception {
        Active me = registerAndVerify("job_seeker");
        long employer = employer("Saver Co", "ICT");
        long a = vacancy(employer, "Job A", "DAR_ES_SALAAM", "FULL_TIME", "ICT", 1, null, null, 0, "ACTIVE");
        long b = vacancy(employer, "Job B", "ARUSHA", "FULL_TIME", "ICT", 1, null, null, 0, "ACTIVE");

        ids(me.token()).andExpect(status().isOk()).andExpect(jsonPath("$.vacancyIds", hasSize(0)));
        save(me.token(), a).andExpect(status().isNoContent());
        save(me.token(), a).andExpect(status().isNoContent()); // idempotent
        save(me.token(), b).andExpect(status().isNoContent());
        ids(me.token()).andExpect(jsonPath("$.vacancyIds", hasSize(2)));

        unsave(me.token(), a).andExpect(status().isNoContent());
        unsave(me.token(), a).andExpect(status().isNoContent()); // idempotent
        ids(me.token()).andExpect(jsonPath("$.vacancyIds").value(contains((int) b)));
    }

    @Test
    void onlyOpenJobsCanBeSaved() throws Exception {
        Active me = registerAndVerify("job_seeker");
        long employer = employer("Drafty Co", "ICT");
        long draft = vacancy(employer, "Draft", "DAR_ES_SALAAM", "FULL_TIME", "ICT", 1, null, null, 0, "DRAFT");
        save(me.token(), draft).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("JOB_NOT_FOUND"));
        save(me.token(), 999_999_999L).andExpect(status().isNotFound());
    }

    @Test
    void jobSeekersOnly() throws Exception {
        mvc.perform(get("/api/me/saved-jobs/ids")).andExpect(status().isUnauthorized());
        Active employerUser = registerAndVerify("employer");
        ids(employerUser.token()).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("JOB_SEEKERS_ONLY"));
    }
}
