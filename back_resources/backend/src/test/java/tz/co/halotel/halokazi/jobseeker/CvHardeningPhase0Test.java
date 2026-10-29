package tz.co.halotel.halokazi.jobseeker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tz.co.halotel.halokazi.common.exception.BusinessRuleException;
import tz.co.halotel.halokazi.common.exception.GlobalExceptionHandler;
import tz.co.halotel.halokazi.jobseeker.controller.CvController;
import tz.co.halotel.halokazi.jobseeker.dto.cv.*;
import tz.co.halotel.halokazi.jobseeker.mapper.CvMapper;
import tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper;
import tz.co.halotel.halokazi.jobseeker.model.*;
import tz.co.halotel.halokazi.jobseeker.repository.CvDataStore;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.CvPdfRenderer;
import tz.co.halotel.halokazi.jobseeker.service.CvService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Red tests for the CV Builder invariants listed in the hardening plan. */
class CvHardeningPhase0Test {

    private record Fixture(CvDataStore cvStore, CvService service) {}

    @FunctionalInterface
    private interface SectionSaver<R> {
        CvDetailDto save(CvService service, Long cvId, int expectedVersion, List<R> requests);
    }

    private Fixture fixture() {
        CvDataStore cvs = new CvDataStore();
        JobSeekerDataStore profiles = new JobSeekerDataStore();
        CvService service = new CvService(cvs, profiles, new CurrentJobSeekerProvider(),
                new CvMapper(), new CvPrefillMapper(), new tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties());
        return new Fixture(cvs, service);
    }

    @Test
    void mapperKeepsIdsNullForNewItemsAcrossAllCvSections() {
        CvMapper mapper = new CvMapper();
        assertAll(
                () -> assertNull(mapper.toExperiences(List.of(new CvExperienceItemRequest())).getFirst().getId()),
                () -> assertNull(mapper.toEducations(List.of(new CvEducationItemRequest())).getFirst().getId()),
                () -> assertNull(mapper.toSkills(List.of(new CvSkillItemRequest())).getFirst().getId()),
                () -> assertNull(mapper.toCertifications(List.of(new CvCertificationItemRequest())).getFirst().getId()),
                () -> assertNull(mapper.toLanguageEntries(List.of(new CvLanguageItemRequest())).getFirst().getId()),
                () -> assertNull(mapper.toProjects(List.of(new CvProjectItemRequest())).getFirst().getId()),
                () -> assertNull(mapper.toReferences(List.of(new CvReferenceItemRequest())).getFirst().getId())
        );
    }

    @TestFactory
    Stream<DynamicTest> newItemsWithoutIdsCanBeAddedToEveryCvSection() {
        return Stream.of(
                DynamicTest.dynamicTest("experience", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-experience"));
                    CvExperienceItemRequest item = new CvExperienceItemRequest(); item.setCompanyName("Example Ltd"); item.setJobTitle("Analyst"); item.setStartDate(LocalDate.of(2024, 1, 1));
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveExperiences(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getExperiences().getFirst().getId());
                }),
                DynamicTest.dynamicTest("education", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-education"));
                    CvEducationItemRequest item = new CvEducationItemRequest(); item.setInstitutionName("Example University"); item.setQualification("Degree");
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveEducations(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getEducations().getFirst().getId());
                }),
                DynamicTest.dynamicTest("skills", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-skill"));
                    CvSkillItemRequest item = new CvSkillItemRequest(); item.setSkillName("Analysis");
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveSkills(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getSkills().getFirst().getId());
                }),
                DynamicTest.dynamicTest("certifications", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-certification"));
                    CvCertificationItemRequest item = new CvCertificationItemRequest(); item.setCertificationName("Certificate"); item.setIssuingOrganization("Issuer");
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveCertifications(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getCertifications().getFirst().getId());
                }),
                DynamicTest.dynamicTest("languages", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-language"));
                    CvLanguageItemRequest item = new CvLanguageItemRequest(); item.setLanguageName("Swahili");
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveLanguages(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getLanguages().getFirst().getId());
                }),
                DynamicTest.dynamicTest("projects", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-project"));
                    CvProjectItemRequest item = new CvProjectItemRequest(); item.setTitle("Example project");
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveProjects(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getProjects().getFirst().getId());
                }),
                DynamicTest.dynamicTest("references", () -> {
                    Fixture f = fixture(); var cv = f.service.createDraft(draft("add-reference"));
                    CvReferenceItemRequest item = new CvReferenceItemRequest(); item.setFullName("Example Referee");
                    CvDetailDto saved = assertDoesNotThrow(() -> f.service.saveReferences(cv.getCvId(), cv.getVersion(), List.of(item)));
                    assertNotNull(saved.getReferences().getFirst().getId());
                })
        );
    }

    @Test
    void prefillIdsAreLocalToTheNewCvSnapshot() {
        JobSeekerDataStore profiles = new JobSeekerDataStore();
        CvPrefillMapper mapper = new CvPrefillMapper();
        Long firstId = mapper.fromWorkExperiences(profiles.getWorkExperiences(1L)).getFirst().getId();
        assertEquals(1L, firstId);
    }

    @Test
    void sortOrdersAreRenumberedByListPosition() {
        CvExperienceItemRequest first = new CvExperienceItemRequest(); first.setSortOrder(17);
        CvExperienceItemRequest second = new CvExperienceItemRequest(); second.setSortOrder(4);
        List<CvExperience> mapped = new CvMapper().toExperiences(List.of(first, second));
        assertEquals(List.of(1, 2), mapped.stream().map(CvExperience::getSortOrder).toList());
    }

    @TestFactory
    Stream<DynamicTest> updatingByIdAndOmittingAnItemWorksInEveryCvSection() {
        return Stream.of(
                sectionMutation("experience", this::experience, CvService::saveExperiences,
                        CvDetailDto::getExperiences, CvExperience::getId, CvExperience::getCompanyName),
                sectionMutation("education", (id, value) -> {
                    CvEducationItemRequest request = new CvEducationItemRequest(); request.setId(id); request.setInstitutionName(value); request.setQualification("Degree"); return request;
                }, CvService::saveEducations, CvDetailDto::getEducations, CvEducation::getId, CvEducation::getInstitutionName),
                sectionMutation("skill", (id, value) -> {
                    CvSkillItemRequest request = new CvSkillItemRequest(); request.setId(id); request.setSkillName(value); return request;
                }, CvService::saveSkills, CvDetailDto::getSkills, CvSkill::getId, CvSkill::getSkillName),
                sectionMutation("certification", (id, value) -> {
                    CvCertificationItemRequest request = new CvCertificationItemRequest(); request.setId(id); request.setCertificationName(value); request.setIssuingOrganization("Issuer"); return request;
                }, CvService::saveCertifications, CvDetailDto::getCertifications, CvCertification::getId, CvCertification::getCertificationName),
                sectionMutation("language", (id, value) -> {
                    CvLanguageItemRequest request = new CvLanguageItemRequest(); request.setId(id); request.setLanguageName(value); return request;
                }, CvService::saveLanguages, CvDetailDto::getLanguages, CvLanguageEntry::getId, CvLanguageEntry::getLanguageName),
                sectionMutation("project", (id, value) -> {
                    CvProjectItemRequest request = new CvProjectItemRequest(); request.setId(id); request.setTitle(value); return request;
                }, CvService::saveProjects, CvDetailDto::getProjects, CvProject::getId, CvProject::getTitle),
                sectionMutation("reference", (id, value) -> {
                    CvReferenceItemRequest request = new CvReferenceItemRequest(); request.setId(id); request.setFullName(value); return request;
                }, CvService::saveReferences, CvDetailDto::getReferences, CvReference::getId, CvReference::getFullName)
        );
    }

    @Test
    void duplicateRequestIdsAreRejectedAndForeignIdsRemain422() {
        Fixture f = fixture();
        CvDetailDto draft = f.service.createDraft(draft("duplicate-ids"));
        Long existingId = draft.getExperiences().getFirst().getId();
        CvExperienceItemRequest one = experience(existingId, "One");
        CvExperienceItemRequest two = experience(existingId, "Two");
        assertThrows(IllegalArgumentException.class, () -> f.service.saveExperiences(draft.getCvId(), draft.getVersion(), List.of(one, two)));
        BusinessRuleException foreign = assertThrows(BusinessRuleException.class,
                () -> f.service.saveExperiences(draft.getCvId(), draft.getVersion(), List.of(experience(999_999L, "Foreign"))));
        assertEquals(422, foreign.getHttpStatus().value());
    }

    @Test
    void duplicateSectionIdsReturn400AndForeignIdsReturn422() throws Exception {
        Fixture f = fixture();
        CvDetailDto draft = f.service.createDraft(draft("request-id-errors"));
        Long existingId = draft.getExperiences().getFirst().getId();
        String duplicateItems = "[{\"id\":" + existingId + ",\"companyName\":\"One\",\"jobTitle\":\"Analyst\",\"startDate\":\"2023-01-01\"},"
                + "{\"id\":" + existingId + ",\"companyName\":\"Two\",\"jobTitle\":\"Analyst\",\"startDate\":\"2023-01-01\"}]";
        MockMvc mvc = mockMvc(f.service);
        mvc.perform(put("/api/v1/jobseeker/cvs/" + draft.getCvId() + "/experience")
                        .header("If-Match", draft.getVersion()).contentType(MediaType.APPLICATION_JSON).content(duplicateItems))
                .andExpect(status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false));

        mvc.perform(put("/api/v1/jobseeker/cvs/" + draft.getCvId() + "/experience")
                        .header("If-Match", draft.getVersion()).contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"id\":999999,\"companyName\":\"Foreign\",\"jobTitle\":\"Analyst\",\"startDate\":\"2023-01-01\"}]"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false));
    }

    @Test
    void templateEndpointCanChangeACompletedCv() throws Exception {
        Fixture f = fixture();
        Cv completed = f.cvStore.getCv(1L);
        completed.setLastStep(CvLastStep.SKILLS);
        Cv versioned = f.cvStore.saveCv(completed, completed.getVersion());
        MockMvc mvc = mockMvc(f.service);
        mvc.perform(put("/api/v1/jobseeker/cvs/1/template")
                        .header("If-Match", String.valueOf(versioned.getVersion()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"templateCode\":\"MODERN\"}"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.lastStep").value("SKILLS"));
    }

    @Test
    void missingSectionVisibilityKeepsItsCurrentValueAndUnknownCodesAreRejected() {
        Fixture f = fixture();
        CvDetailDto draft = f.service.createDraft(draft("section-config"));
        CvSectionUpdateRequest update = new CvSectionUpdateRequest();
        update.setSectionCode("SUMMARY"); update.setDisplayOrder(null); update.setVisible(null);
        CvDetailDto saved = f.service.saveSectionConfiguration(draft.getCvId(), draft.getVersion(), List.of(update));
        CvDetailDto.SectionConfig summary = saved.getSectionConfig().stream().filter(s -> s.getSectionCode().equals("SUMMARY")).findFirst().orElseThrow();
        assertAll(() -> assertTrue(summary.isVisible()), () -> assertEquals(2, summary.getDisplayOrder()));

        CvSectionUpdateRequest invalid = new CvSectionUpdateRequest();
        invalid.setSectionCode("NOT_A_SECTION"); invalid.setDisplayOrder(1); invalid.setVisible(true);
        assertThrows(IllegalArgumentException.class,
                () -> f.service.saveSectionConfiguration(draft.getCvId(), saved.getVersion(), List.of(invalid)));
    }

    @Test
    void sectionDisplayOrderCanRepresentNullSafely() throws Exception {
        Field field = CvSectionUpdateRequest.class.getDeclaredField("displayOrder");
        assertEquals(Integer.class, field.getType());
    }

    @Test
    void whitespaceOnlyCreateTitleIsRejected() throws Exception {
        Fixture f = fixture();
        mockMvc(f.service).perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/jobseeker/cvs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \",\"templateCode\":\"CLASSIC\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void newDraftIsNotDefaultAndOnlyACompletedCvCanBecomeDefault() {
        Fixture f = fixture();
        Cv draft = new Cv(); draft.setProfileId(77L); draft.setCvName("Draft"); draft.setTemplateId(1L); draft.setTemplateCode("CLASSIC"); draft.setTemplateName("Classic"); draft.setStatus(CvStatus.DRAFT);
        Cv saved = f.cvStore.createCv(draft, 10);
        assertFalse(saved.isDefault());
        assertThrows(BusinessRuleException.class, () -> f.cvStore.setDefaultForProfile(77L, saved.getCvId(), saved.getVersion()));
    }

    @Test
    void firstCompletedCvBecomesDefaultWhenProfileHasNone() {
        JobSeekerDataStore profiles = new JobSeekerDataStore();
        JobSeekerProfile otherProfile = new JobSeekerProfile(); otherProfile.setProfileId(77L); otherProfile.setFirstName("Other"); otherProfile.setLastName("Person"); otherProfile.setContactEmail("other@example.test"); otherProfile.setHeadline("Analyst");
        profiles.saveProfile(otherProfile);
        profiles.saveWorkExperience(77L, work());
        Education education = new Education(); education.setInstitutionName("University"); education.setQualification("Degree"); profiles.saveEducation(77L, education);
        profiles.saveSkill(77L, new ProfileSkill(null, 77L, 500L, "Analysis", "ADVANCED", 2.0));
        CvDataStore cvs = new CvDataStore();
        CurrentJobSeekerProvider provider = new CurrentJobSeekerProvider() { @Override public Long getCurrentProfileId() { return 77L; } };
        CvService service = new CvService(cvs, profiles, provider, new CvMapper(), new CvPrefillMapper(), new tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties());
        CvDetailDto draft = service.createDraft(draft("first-completed"));
        Cv detached = cvs.getCv(draft.getCvId()); detached.setDefault(false);
        Cv clearedDefault = cvs.saveCv(detached, detached.getVersion());
        assertTrue(service.finalizeCv(draft.getCvId(), clearedDefault.getVersion()).isValid());
        assertTrue(service.getCv(draft.getCvId()).isDefault());
    }

    @Test
    void changingDefaultDoesNotChangeTheFormerDefaultsVersion() {
        CvDataStore cvs = new CvDataStore();
        int oldVersion = cvs.getCv(1L).getVersion();
        cvs.setDefaultForProfile(1L, 2L, cvs.getCv(2L).getVersion());
        assertEquals(oldVersion, cvs.getCv(1L).getVersion());
    }

    @Test
    void deletingDefaultPromotesMostRecentCompletedCvNotADraft() {
        CvDataStore cvs = new CvDataStore();
        cvs.softDeleteCv(1L, 1L, 1);
        assertTrue(cvs.getCv(2L).isDefault());
        assertFalse(cvs.getCv(3L).isDefault());
    }

    @Test
    void cvVersionRemainsPrimitiveInt() throws Exception {
        assertEquals(int.class, Cv.class.getDeclaredField("version").getType());
    }

    private CreateCvDraftRequest draft(String title) {
        CreateCvDraftRequest request = new CreateCvDraftRequest(); request.setTitle("Phase0 " + title); request.setTemplateCode("CLASSIC"); return request;
    }

    private CvExperienceItemRequest experience(Long id, String company) {
        CvExperienceItemRequest request = new CvExperienceItemRequest(); request.setId(id); request.setCompanyName(company); request.setJobTitle("Analyst"); request.setStartDate(LocalDate.of(2023, 1, 1)); return request;
    }

    private <R, T> DynamicTest sectionMutation(String section,
                                               BiFunction<Long, String, R> requestFactory,
                                               SectionSaver<R> saver,
                                               Function<CvDetailDto, List<T>> items,
                                               Function<T, Long> itemId,
                                               Function<T, String> itemValue) {
        return DynamicTest.dynamicTest(section, () -> {
            Fixture f = fixture();
            CvDetailDto draft = f.service.createDraft(draft("mutation-" + section));
            String addedValue = "Phase2 added " + section;
            CvDetailDto added = saver.save(f.service, draft.getCvId(), draft.getVersion(), List.of(requestFactory.apply(null, addedValue)));
            T addedItem = items.apply(added).stream().filter(item -> addedValue.equals(itemValue.apply(item))).findFirst().orElseThrow();
            Long id = itemId.apply(addedItem);
            assertNotNull(id);

            String updatedValue = "Phase2 updated " + section;
            CvDetailDto updated = saver.save(f.service, draft.getCvId(), added.getVersion(), List.of(requestFactory.apply(id, updatedValue)));
            assertEquals(updatedValue, itemValue.apply(items.apply(updated).getFirst()));

            CvDetailDto removed = saver.save(f.service, draft.getCvId(), updated.getVersion(), List.of());
            assertTrue(items.apply(removed).isEmpty());
        });
    }

    private WorkExperience work() {
        WorkExperience experience = new WorkExperience(); experience.setCompanyName("Example Company"); experience.setJobTitle("Analyst"); experience.setStartDate(LocalDate.of(2023, 1, 1)); return experience;
    }

    private MockMvc mockMvc(CvService service) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        return MockMvcBuilders.standaloneSetup(new CvController(service, new CvPdfRenderer()))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
