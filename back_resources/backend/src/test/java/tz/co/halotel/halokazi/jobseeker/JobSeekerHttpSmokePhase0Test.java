package tz.co.halotel.halokazi.jobseeker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tz.co.halotel.halokazi.common.exception.GlobalExceptionHandler;
import tz.co.halotel.halokazi.jobseeker.controller.*;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Endpoint smoke coverage for the non-CV Jobseeker controllers without a Spring/DB context. */
class JobSeekerHttpSmokePhase0Test {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileStrengthService strength = new ProfileStrengthService(store);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(
                        new JobSeekerProfileController(new JobSeekerProfileService(store, strength), strength, store),
                        new JobSeekerEducationController(new EducationService(store, strength)),
                        new JobSeekerWorkExperienceController(new WorkExperienceService(store, strength)),
                        new JobSeekerCertificationController(new CertificationService(store, strength)),
                        new JobSeekerSkillController(new SkillService(store, strength)),
                        new JobSeekerLanguageController(new LanguageService(store, strength)),
                        new JobSeekerProjectController(new ProjectService(store)),
                        new JobSeekerReferenceController(new ReferenceService(store)),
                        new JobSeekerSavedJobController(new SavedJobService(store)),
                        new JobSeekerApplicationController(new JobApplicationService(store)),
                        new JobSeekerAlertController(new JobAlertService(store)))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validProfileAndDashboardRequestsReturnWrappedSuccess() throws Exception {
        assertAll(
                () -> ok(get("/api/v1/jobseeker/profile")),
                () -> ok(get("/api/v1/jobseeker/profile/1")),
                () -> ok(get("/api/v1/jobseeker/profile/detail")),
                () -> ok(get("/api/v1/jobseeker/profile/1/detail")),
                () -> ok(get("/api/v1/jobseeker/profile/strength")),
                () -> ok(get("/api/v1/jobseeker/profile/stats")),
                () -> ok(get("/api/v1/jobseeker/profile/recommendations")),
                () -> ok(put("/api/v1/jobseeker/profile/personal-details").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Mwangi\"}")),
                () -> ok(patch("/api/v1/jobseeker/profile/headline-summary").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"headline\":\"Marketing Specialist\",\"professionalSummary\":\"A detailed professional summary for testing.\"}")),
                () -> ok(patch("/api/v1/jobseeker/profile/visibility").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visibility\":\"Public\"}")),
                () -> ok(patch("/api/v1/jobseeker/profile/picture").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pictureUrl\":\"https://example.test/profile.jpg\"}"))
        );
    }

    @Test
    void validProfileSectionEndpointsReturnWrappedSuccess() throws Exception {
        assertAll(
                () -> ok(get("/api/v1/jobseeker/education?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/education?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institutionName\":\"Example University\",\"qualification\":\"Bachelor's Degree\"}")),
                () -> ok(put("/api/v1/jobseeker/education/1?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institutionName\":\"Updated University\",\"qualification\":\"Bachelor's Degree\"}")),
                () -> ok(delete("/api/v1/jobseeker/education/1?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/work-experience?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/work-experience?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"Example Ltd\",\"jobTitle\":\"Analyst\",\"startDate\":\"2024-01-01\"}")),
                () -> ok(put("/api/v1/jobseeker/work-experience/1?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"Updated Ltd\",\"jobTitle\":\"Analyst\",\"startDate\":\"2023-01-01\"}")),
                () -> ok(delete("/api/v1/jobseeker/work-experience/1?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/certifications?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/certifications?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"certificationName\":\"Certificate\",\"issuingOrganization\":\"Issuer\"}")),
                () -> ok(put("/api/v1/jobseeker/certifications/1?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"certificationName\":\"Updated Certificate\",\"issuingOrganization\":\"Issuer\"}")),
                () -> ok(delete("/api/v1/jobseeker/certifications/1?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/skills?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/skills?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skillId\":999,\"skillName\":\"Testing\",\"proficiency\":\"ADVANCED\"}")),
                () -> ok(delete("/api/v1/jobseeker/skills/101?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/languages?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/languages?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"languageId\":999,\"languageName\":\"Kiswahili\",\"isoCode\":\"sw\",\"proficiency\":\"FLUENT\"}")),
                () -> ok(delete("/api/v1/jobseeker/languages/1?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/projects?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/projects?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Example Project\",\"role\":\"Lead\"}")),
                () -> ok(delete("/api/v1/jobseeker/projects/1?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/references?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/references?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Example Referee\"}")),
                () -> ok(delete("/api/v1/jobseeker/references/1?profileId=1"))
        );
    }

    @Test
    void validSavedJobApplicationAndAlertEndpointsReturnWrappedSuccess() throws Exception {
        assertAll(
                () -> ok(get("/api/v1/jobseeker/saved-jobs?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/saved-jobs?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vacancyId\":99999,\"jobTitle\":\"Analyst\",\"companyName\":\"Example Ltd\"}")),
                () -> ok(delete("/api/v1/jobseeker/saved-jobs/99999?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/applications?profileId=1")),
                () -> ok(get("/api/v1/jobseeker/applications/1?profileId=1")),
                () -> ok(post("/api/v1/jobseeker/applications?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vacancyId\":99999,\"cvId\":1,\"coverLetter\":\"Please consider my application.\"}")),
                () -> ok(get("/api/v1/jobseeker/alerts?profileId=1")),
                () -> ok(put("/api/v1/jobseeker/alerts?profileId=1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":true,\"receiveEmailAlerts\":true,\"receiveSmsAlerts\":false,\"frequency\":\"DAILY\"}")),
                () -> ok(patch("/api/v1/jobseeker/alerts/toggle?profileId=1"))
        );
    }

    private void ok(MockHttpServletRequestBuilder request) throws Exception {
        mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
    }
}
