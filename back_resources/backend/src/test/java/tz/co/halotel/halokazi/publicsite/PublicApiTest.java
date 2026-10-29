package tz.co.halotel.halokazi.publicsite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

/**
 * The public landing-page API. Every test builds its own employers and vacancies (rolled back), and
 * searches by a per-test keyword, so assertions hold no matter what demo data the database contains.
 */
class PublicApiTest extends AbstractIntegrationTest {

    private String token;

    @BeforeEach
    void newToken() {
        token = "zq" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    // ------------------------------------------------------------------ fixtures

    /** Three open jobs, one draft, and one job from a suspended employer, all titled with the test token. */
    private long[] board() {
        long bank = employer("Test Bank " + token, "FINANCE");
        long tel = employer("Tel Co " + token, "TELECOM");
        long a = vacancy(bank, "Accountant " + token, "DAR_ES_SALAAM", "FULL_TIME", "FINANCE", 3, 1_500_000L,
                2_000_000L, 2, "ACTIVE");
        long b = vacancy(tel, "Engineer " + token, "ARUSHA", "CONTRACT", "ENGINEERING", 6, 3_500_000L, null, 20,
                "ACTIVE");
        long c = vacancy(tel, "Intern " + token, "DAR_ES_SALAAM", "INTERNSHIP", "ICT", 0, null, null, 0, "ACTIVE");
        long draft = vacancy(tel, "Draft " + token, "DAR_ES_SALAAM", "FULL_TIME", "ICT", 0, null, null, 0, "DRAFT");
        long suspended = employer("Gone Ltd " + token, "ICT");
        vacancy(suspended, "Hidden " + token, "DAR_ES_SALAAM", "FULL_TIME", "ICT", 0, null, null, 0, "ACTIVE");
        exec("update app_user set account_status = 'SUSPENDED' where user_id = "
                + "(select user_id from employer where employer_id = ?)", suspended);
        return new long[] { a, b, c, draft };
    }

    private ResultActions jobs(String query) throws Exception {
        return mvc.perform(get("/api/public/jobs?q=" + token + (query.isEmpty() ? "" : "&" + query)));
    }

    // ------------------------------------------------------------------ jobs

    @Test
    void listsOnlyOpenJobsNewestFirst() throws Exception {
        long[] ids = board();
        jobs("").andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                // newest first: posted 0, 2 and 20 days ago
                .andExpect(jsonPath("$.items[*].id").value(contains((int) ids[2], (int) ids[0], (int) ids[1])))
                .andExpect(jsonPath("$.items[*].id", not(hasItem((int) ids[3]))));
    }

    @Test
    void jobCardShape() throws Exception {
        long[] ids = board();
        jobs("type=FULL_TIME").andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id").value((int) ids[0]))
                .andExpect(jsonPath("$.items[0].title").value("Accountant " + token))
                .andExpect(jsonPath("$.items[0].company.name").value("Test Bank " + token))
                .andExpect(jsonPath("$.items[0].company.initials").value("TBZ"))
                .andExpect(jsonPath("$.items[0].region.code").value("DAR_ES_SALAAM"))
                .andExpect(jsonPath("$.items[0].region.nameEn").value("Dar es Salaam"))
                .andExpect(jsonPath("$.items[0].employmentType.code").value("FULL_TIME"))
                .andExpect(jsonPath("$.items[0].category.code").value("FINANCE"))
                .andExpect(jsonPath("$.items[0].salary.min").value(1500000))
                .andExpect(jsonPath("$.items[0].salary.max").value(2000000))
                .andExpect(jsonPath("$.items[0].salary.currency").value("TZS"))
                .andExpect(jsonPath("$.items[0].salary.period").value("MONTH"))
                .andExpect(jsonPath("$.items[0].publishedAt", endsWith("Z")))
                .andExpect(jsonPath("$.items[0].applicationDeadline", endsWith("Z")));
    }

    @Test
    void jobWithoutSalaryHasNullSalary() throws Exception {
        board();
        jobs("type=INTERNSHIP").andExpect(jsonPath("$.items[0].salary").doesNotExist());
    }

    @Test
    void filtersNarrowResults() throws Exception {
        long[] ids = board();
        jobs("region=ARUSHA").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[1])));
        jobs("region=dar_es_salaam").andExpect(jsonPath("$.totalItems").value(2)); // codes are case-insensitive
        jobs("type=CONTRACT").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[1])));
        jobs("category=ICT").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[2])));
        jobs("experience=ENTRY").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[2])));
        jobs("experience=MID").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[0])));
        jobs("experience=SENIOR").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[1])));
        jobs("salary=FROM_1M_TO_3M").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[0])));
        jobs("salary=OVER_3M").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[1])));
        jobs("salary=UNDER_500K").andExpect(jsonPath("$.totalItems").value(0));
        jobs("posted=LAST_24_HOURS").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[2])));
        jobs("posted=LAST_7_DAYS").andExpect(jsonPath("$.totalItems").value(2));
        jobs("posted=last_30_days").andExpect(jsonPath("$.totalItems").value(3));
        jobs("region=DAR_ES_SALAAM&type=FULL_TIME&salary=FROM_1M_TO_3M")
                .andExpect(jsonPath("$.items[*].id").value(contains((int) ids[0])));
    }

    @Test
    void keywordMatchesTitleOrCompany() throws Exception {
        long[] ids = board();
        mvc.perform(get("/api/public/jobs").param("q", "ACCOUNTANT " + token))
                .andExpect(jsonPath("$.items[*].id").value(contains((int) ids[0])));
        mvc.perform(get("/api/public/jobs").param("q", "tel co " + token)).andExpect(jsonPath("$.totalItems").value(2));
        // LIKE wildcards in the keyword are literal characters, not "match anything"
        mvc.perform(get("/api/public/jobs").param("q", token.substring(0, 4) + "%" + token.substring(5)))
                .andExpect(jsonPath("$.totalItems").value(0));
        mvc.perform(get("/api/public/jobs").param("q", token.substring(0, 4) + "_" + token.substring(5)))
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    void paginates() throws Exception {
        long[] ids = board();
        jobs("size=2").andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
        jobs("size=2&page=2").andExpect(jsonPath("$.items[*].id").value(contains((int) ids[1])))
                .andExpect(jsonPath("$.page").value(2));
        jobs("size=2&page=3").andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalItems").value(3));
    }

    @Test
    void emptySearchReturnsEmptyPage() throws Exception {
        jobs("").andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void badParametersAre400() throws Exception {
        jobs("experience=EXPERT").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("experience"));
        jobs("salary=LOTS").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("salary"));
        jobs("posted=YESTERDAY").andExpect(jsonPath("$.fieldErrors[0].field").value("posted"));
        jobs("page=0").andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors[0].field").value("page"));
        jobs("size=51").andExpect(jsonPath("$.fieldErrors[0].field").value("size"));
        jobs("page=abc").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("page"));
    }

    @Test
    void unknownLookupCodeSimplyMatchesNothing() throws Exception {
        board();
        jobs("region=ATLANTIS").andExpect(status().isOk()).andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    void jobDetail() throws Exception {
        long[] ids = board();
        mvc.perform(get("/api/public/jobs/" + ids[0])).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) ids[0]))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.requirements").value("reqs"))
                .andExpect(jsonPath("$.qualifications").value("quals"))
                .andExpect(jsonPath("$.workExperience").value("some"))
                .andExpect(jsonPath("$.minExperienceYears").value(3))
                .andExpect(jsonPath("$.numberOfPositions").value(2))
                .andExpect(jsonPath("$.minEducationLevel.code").value("BACHELOR"))
                .andExpect(jsonPath("$.company.name").value("Test Bank " + token));
    }

    @Test
    void draftsAndMissingJobsAre404() throws Exception {
        long[] ids = board();
        mvc.perform(get("/api/public/jobs/" + ids[3])).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("JOB_NOT_FOUND"));
        mvc.perform(get("/api/public/jobs/999999999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/public/jobs/abc")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("id"));
    }

    @Test
    void publicEndpointsIgnoreAuthentication() throws Exception {
        mvc.perform(get("/api/public/jobs").header(HttpHeaders.AUTHORIZATION, "Bearer garbage"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------------------------------ employers

    @Test
    void topEmployersRankedByOpenJobs() throws Exception {
        long big = employer("Big Hirer " + token, "ICT");
        for (int i = 0; i < 9; i++) { // more than any demo employer
            vacancy(big, "Role " + i + " " + token, "DAR_ES_SALAAM", "FULL_TIME", "ICT", 1, null, null, 1, "ACTIVE");
        }
        long draftOnly = employer("Drafty " + token, "ICT");
        vacancy(draftOnly, "Draft " + token, "DAR_ES_SALAAM", "FULL_TIME", "ICT", 1, null, null, 0, "DRAFT");

        mvc.perform(get("/api/public/employers/top")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].id").value((int) big))
                .andExpect(jsonPath("$[0].name").value("Big Hirer " + token))
                .andExpect(jsonPath("$[0].initials").value("BHZ"))
                .andExpect(jsonPath("$[0].industry.code").value("ICT"))
                .andExpect(jsonPath("$[0].openJobs").value(9))
                .andExpect(jsonPath("$[*].openJobs", everyItem(org.hamcrest.Matchers.greaterThan(0))));
        mvc.perform(get("/api/public/employers/top?limit=20"))
                .andExpect(jsonPath("$[*].id", not(hasItem((int) draftOnly))));
        mvc.perform(get("/api/public/employers/top?limit=1")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/public/employers/top?limit=0")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("limit"));
        mvc.perform(get("/api/public/employers/top?limit=21")).andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ stats

    @Test
    void statsCountLiveData() throws Exception {
        String before = body(mvc.perform(get("/api/public/stats")).andExpect(status().isOk()).andReturn());
        board(); // +3 open jobs, +2 visible employers (the suspended one is excluded), +3 published recently
        registerAndVerify("job_seeker"); // +1 job seeker
        em.flush(); // JPA writes are still pending in this shared test transaction; the stats query is plain SQL
        String after = body(mvc.perform(get("/api/public/stats")).andReturn());

        assertThat(delta(before, after, "openJobs")).isEqualTo(3);
        assertThat(delta(before, after, "employers")).isEqualTo(2);
        assertThat(delta(before, after, "jobSeekers")).isEqualTo(1);
        // published in the last 30 days, any status but DELETED: 3 open + the suspended employer's job
        assertThat(delta(before, after, "jobsPostedLast30Days")).isEqualTo(4);
    }

    private static long delta(String before, String after, String field) {
        return ((Number) JsonPath.read(after, "$." + field)).longValue()
                - ((Number) JsonPath.read(before, "$." + field)).longValue();
    }

    // ------------------------------------------------------------------ lookups & CV templates

    @Test
    void lookups() throws Exception {
        mvc.perform(get("/api/public/lookups")).andExpect(status().isOk())
                .andExpect(jsonPath("$.regions", hasSize(31)))
                .andExpect(jsonPath("$.regions[*].code", hasItem("DAR_ES_SALAAM")))
                .andExpect(jsonPath("$.regions[0].nameSw").exists())
                .andExpect(jsonPath("$.employmentTypes", hasSize(6)))
                .andExpect(jsonPath("$.employmentTypes[0].code").value("FULL_TIME"))
                .andExpect(jsonPath("$.categories", hasSize(18)))
                .andExpect(jsonPath("$.categories[0].code").value("ICT"))
                .andExpect(jsonPath("$.experienceLevels[*].code").value(contains("ENTRY", "JUNIOR", "MID", "SENIOR")))
                .andExpect(jsonPath("$.salaryRanges[*].code")
                        .value(contains("UNDER_500K", "FROM_500K_TO_1M", "FROM_1M_TO_3M", "OVER_3M")))
                .andExpect(jsonPath("$.datePosted[*].code")
                        .value(contains("LAST_24_HOURS", "LAST_7_DAYS", "LAST_30_DAYS")))
                .andExpect(jsonPath("$.datePosted[0].label").value("Last 24 hours"));
    }

    @Test
    void cvTemplates() throws Exception {
        mvc.perform(get("/api/public/cv-templates")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].code").value(contains("CLASSIC", "MODERN", "PROFESSIONAL", "MINIMAL", "GRADUATE")))
                .andExpect(jsonPath("$[0].nameEn").value("Classic"))
                .andExpect(jsonPath("$[0].nameSw").value("Kawaida"))
                .andExpect(jsonPath("$[0].scheme").value("light"))
                .andExpect(jsonPath("$[0].accent").value("#14213D"))
                .andExpect(jsonPath("$[2].scheme").value("dark"));
    }

    @Test
    void inactiveCvTemplatesAreHidden() throws Exception {
        exec("update cv_template set status = 'INACTIVE' where code = 'GRADUATE'");
        mvc.perform(get("/api/public/cv-templates")).andExpect(jsonPath("$", hasSize(4)));
    }
}
