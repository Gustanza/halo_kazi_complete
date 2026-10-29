package tz.co.halotel.halokazi.jobseeker.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tz.co.halotel.halokazi.common.exception.GlobalExceptionHandler;
import tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties;
import tz.co.halotel.halokazi.jobseeker.mapper.CvMapper;
import tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper;
import tz.co.halotel.halokazi.jobseeker.model.Cv;
import tz.co.halotel.halokazi.jobseeker.model.CvStatus;
import tz.co.halotel.halokazi.jobseeker.repository.CvDataStore;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.CvPdfRenderer;
import tz.co.halotel.halokazi.jobseeker.service.CvService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Full endpoint flow through Spring MVC with the real CV services and in-memory stores. */
class CvHttpFlowTest {

    private static final String BASE = "/api/v1/jobseeker/cvs";
    private final ObjectMapper json = new ObjectMapper();
    private CvDataStore cvStore;
    private JobSeekerDataStore profileStore;
    private CvConfigProperties properties;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        cvStore = new CvDataStore();
        profileStore = new JobSeekerDataStore();
        properties = new CvConfigProperties();
        properties.setMaxPerProfile(6);
        properties.setMaxPageSize(2);
        CurrentJobSeekerProvider provider = new CurrentJobSeekerProvider();
        CvService service = new CvService(cvStore, profileStore, provider, new CvMapper(), new CvPrefillMapper(), properties);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new CvController(service, new CvPdfRenderer()))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void allBuilderAndMyCvsEndpointsAndErrorCases() throws Exception {
        JsonNode response = json(call(get(BASE + "/templates"), 200));
        assertEquals(5, response.path("data").size());

        response = json(call(post(BASE).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"HTTP CV\",\"templateCode\":\"PROFESSIONAL\"}"), 201));
        long cvId = response.path("data").path("cvId").asLong();
        int version = response.path("data").path("version").asInt();
        assertEquals("DRAFT", response.path("data").path("status").asText());
        response = json(call(get(BASE + "/" + cvId), 200));
        assertEquals("John", response.path("data").path("personalInfo").path("firstName").asText());
        profileStore.getProfile(1L).setFirstName("Changed profile name");
        response = json(call(get(BASE + "/" + cvId), 200));
        assertEquals("John", response.path("data").path("personalInfo").path("firstName").asText());

        String personal = "{\"firstName\":\"John\",\"lastName\":\"Mwangi\",\"contactEmail\":\"john.mwangi@halokazi.co.tz\","
                + "\"headline\":\"Marketing Specialist\",\"professionalSummary\":\"Profile snapshot\",\"phone\":\"+255 712 345 678\"}";
        response = json(call(put(BASE + "/" + cvId + "/personal-info").header("If-Match", version)
                .contentType(MediaType.APPLICATION_JSON).content(personal), 200));
        version = response.path("data").path("version").asInt();

        String[] sections = {"experiences", "educations", "skills", "certifications", "languages", "projects", "references"};
        String[] paths = {"experience", "education", "skills", "certifications", "languages", "projects", "references"};
        for (int i = 0; i < sections.length; i++) {
            String body = json.writeValueAsString(response.path("data").path(sections[i]));
            response = json(call(put(BASE + "/" + cvId + "/" + paths[i]).header("If-Match", version)
                    .contentType(MediaType.APPLICATION_JSON).content(body), 200));
            version = response.path("data").path("version").asInt();
        }
        response = json(call(put(BASE + "/" + cvId + "/sections").header("If-Match", version)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"sectionCode\":\"SUMMARY\",\"visible\":true,\"displayOrder\":1}]"), 200));
        version = response.path("data").path("version").asInt();

        response = json(call(put(BASE + "/" + cvId + "/template").header("If-Match", version)
                .contentType(MediaType.APPLICATION_JSON).content("{\"templateCode\":\"MODERN\"}"), 200));
        version = response.path("data").path("version").asInt();
        response = json(call(post(BASE + "/" + cvId + "/finalize").header("If-Match", version), 200));
        assertTrue(response.path("data").path("valid").asBoolean());
        assertEquals("COMPLETED", response.path("data").path("cv").path("status").asText());

        response = json(call(get(BASE + "?page=0&size=1000"), 200));
        assertEquals(2, response.path("data").path("size").asInt());
        response = json(call(get(BASE + "/" + cvId), 200));
        version = response.path("data").path("version").asInt();
        response = json(call(put(BASE + "/" + cvId + "/title").header("If-Match", version)
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Renamed HTTP CV\"}"), 200));
        version = response.path("data").path("version").asInt();

        response = json(call(post(BASE + "/" + cvId + "/duplicate").header("If-Match", version), 201));
        long duplicateId = response.path("data").path("cvId").asLong();
        int duplicateVersion = response.path("data").path("version").asInt();
        assertEquals("DRAFT", response.path("data").path("status").asText());
        call(put(BASE + "/" + duplicateId + "/default").header("If-Match", duplicateVersion), 422);
        assertFalse(cvStore.getCv(duplicateId).isDefault());
        response = json(call(post(BASE + "/" + duplicateId + "/finalize").header("If-Match", duplicateVersion), 200));
        assertTrue(response.path("data").path("valid").asBoolean());
        response = json(call(get(BASE + "/" + duplicateId), 200));
        duplicateVersion = response.path("data").path("version").asInt();
        response = json(call(put(BASE + "/" + duplicateId + "/default").header("If-Match", duplicateVersion), 200));
        duplicateVersion = response.path("data").path("version").asInt();
        assertEquals(1, cvStore.getCvsByProfileId(1L).stream().filter(Cv::isDefault).count());
        response = json(call(put(BASE + "/" + duplicateId + "/template").header("If-Match", duplicateVersion)
                .contentType(MediaType.APPLICATION_JSON).content("{\"templateCode\":\"MINIMAL\"}"), 200));
        duplicateVersion = response.path("data").path("version").asInt();

        MvcResult download = mvc.perform(get(BASE + "/" + duplicateId + "/download")).andReturn();
        assertEquals(200, download.getResponse().getStatus());
        assertEquals("application/pdf", download.getResponse().getContentType());
        assertTrue(new String(download.getResponse().getContentAsByteArray(), StandardCharsets.US_ASCII).startsWith("%PDF-1.4"));

        call(put(BASE + "/3/personal-info").header("If-Match", 1).contentType(MediaType.APPLICATION_JSON).content("{}"), 400);
        call(get(BASE + "/999999"), 404);
        Cv foreign = new Cv();
        foreign.setProfileId(2L);
        foreign.setTemplateId(1L);
        foreign.setTemplateCode("CLASSIC");
        foreign.setTemplateName("Classic");
        foreign.setCvName("Other jobseeker CV");
        foreign.setStatus(CvStatus.DRAFT);
        Cv savedForeign = cvStore.saveCv(foreign);
        call(get(BASE + "/" + savedForeign.getCvId()), 404);
        call(put(BASE + "/" + cvId + "/template").header("If-Match", version)
                .contentType(MediaType.APPLICATION_JSON).content("{\"templateCode\":\"INVALID\"}"), 400);

        response = json(call(post(BASE).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Incomplete CV\"}"), 201));
        long incompleteId = response.path("data").path("cvId").asLong();
        int incompleteVersion = response.path("data").path("version").asInt();
        String incompletePersonal = "{\"firstName\":\"John\",\"lastName\":\"Mwangi\",\"contactEmail\":\"john@example.test\"}";
        response = json(call(put(BASE + "/" + incompleteId + "/personal-info").header("If-Match", incompleteVersion)
                .contentType(MediaType.APPLICATION_JSON).content(incompletePersonal), 200));
        incompleteVersion = response.path("data").path("version").asInt();
        for (String path : new String[]{"experience", "education", "skills"}) {
            response = json(call(put(BASE + "/" + incompleteId + "/" + path).header("If-Match", incompleteVersion)
                    .contentType(MediaType.APPLICATION_JSON).content("[]"), 200));
            incompleteVersion = response.path("data").path("version").asInt();
        }
        response = json(call(post(BASE + "/" + incompleteId + "/finalize").header("If-Match", incompleteVersion), 422));
        assertFalse(response.path("data").path("valid").asBoolean());
        assertTrue(response.path("data").path("errors").size() >= 4);

        call(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Over limit\"}"), 409);
        call(delete(BASE + "/" + duplicateId).header("If-Match", duplicateVersion), 200);
        call(get(BASE + "/" + duplicateId), 404);
    }

    @Test
    void foreignCvIsHiddenFromAnotherJobseekerOverMvc() throws Exception {
        cvStore = new CvDataStore();
        profileStore = new JobSeekerDataStore();
        CurrentJobSeekerProvider anotherJobseeker = new CurrentJobSeekerProvider() {
            @Override
            public Long getCurrentProfileId() {
                return 2L;
            }
        };
        CvService service = new CvService(cvStore, profileStore, anotherJobseeker,
                new CvMapper(), new CvPrefillMapper(), new CvConfigProperties());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new CvController(service, new CvPdfRenderer()))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        call(get(BASE + "/1"), 404);
    }

    private MockHttpServletResponse call(MockHttpServletRequestBuilder request, int expectedStatus) throws Exception {
        MvcResult result = mvc.perform(request).andReturn();
        assertEquals(expectedStatus, result.getResponse().getStatus(), result.getResponse().getContentAsString());
        return result.getResponse();
    }

    private JsonNode json(MockHttpServletResponse response) throws Exception {
        return json.readTree(response.getContentAsByteArray());
    }
}
