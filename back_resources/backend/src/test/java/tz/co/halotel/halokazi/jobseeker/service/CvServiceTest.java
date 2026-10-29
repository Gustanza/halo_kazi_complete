package tz.co.halotel.halokazi.jobseeker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tz.co.halotel.halokazi.common.exception.BusinessRuleException;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties;
import tz.co.halotel.halokazi.jobseeker.dto.cv.CreateCvDraftRequest;
import tz.co.halotel.halokazi.jobseeker.dto.cv.CvDetailDto;
import tz.co.halotel.halokazi.jobseeker.dto.cv.CvFinalizeResultDto;
import tz.co.halotel.halokazi.jobseeker.mapper.CvMapper;
import tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper;
import tz.co.halotel.halokazi.jobseeker.model.*;
import tz.co.halotel.halokazi.jobseeker.repository.CvDataStore;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CvServiceTest {

    @Mock private JobSeekerDataStore profileStore;
    @Mock private CurrentJobSeekerProvider currentJobSeekerProvider;

    private CvDataStore cvDataStore;
    private CvConfigProperties properties;
    private CvService service;
    private JobSeekerProfile profile;
    private List<WorkExperience> experiences;
    private List<Education> educations;
    private List<ProfileSkill> skills;

    @BeforeEach
    void setUp() {
        cvDataStore = new CvDataStore();
        properties = new CvConfigProperties();
        profile = profile(true);
        experiences = List.of(experience("Original employer"));
        educations = List.of(education());
        skills = List.of(new ProfileSkill(20L, 1L, 2L, "Java", "ADVANCED", 3.0));
        when(currentJobSeekerProvider.getCurrentProfileId()).thenReturn(1L);
        when(profileStore.getProfile(anyLong())).thenReturn(profile);
        when(profileStore.getWorkExperiences(anyLong())).thenReturn(experiences);
        when(profileStore.getEducations(anyLong())).thenReturn(educations);
        when(profileStore.getSkills(anyLong())).thenReturn(skills);
        when(profileStore.getCertifications(anyLong())).thenReturn(List.of());
        when(profileStore.getLanguages(anyLong())).thenReturn(List.of());
        when(profileStore.getProjects(anyLong())).thenReturn(List.of());
        when(profileStore.getReferences(anyLong())).thenReturn(List.of());
        service = new CvService(cvDataStore, profileStore, currentJobSeekerProvider,
                new CvMapper(), new CvPrefillMapper(), properties);
    }

    @Test
    void createDraftCopiesProfileAndSectionSnapshots() {
        CvDetailDto created = service.createDraft(new CreateCvDraftRequest());
        profile.setFirstName("Changed later");
        experiences.getFirst().setCompanyName("Changed employer");

        CvDetailDto readAgain = service.getCv(created.getCvId());
        assertEquals("John", readAgain.getPersonalInfo().getFirstName());
        assertEquals("Original employer", readAgain.getExperiences().getFirst().getCompanyName());
        assertNotSame(profile, readAgain.getPersonalInfo());
        assertEquals("DRAFT", readAgain.getStatus());
    }

    @Test
    void finalizeReturnsEveryMissingRequirementAtOnce() {
        profile = profile(false);
        when(profileStore.getProfile(anyLong())).thenReturn(profile);
        when(profileStore.getWorkExperiences(anyLong())).thenReturn(List.of());
        when(profileStore.getEducations(anyLong())).thenReturn(List.of());
        when(profileStore.getSkills(anyLong())).thenReturn(List.of());
        CvDetailDto draft = service.createDraft(new CreateCvDraftRequest());

        CvFinalizeResultDto result = service.finalizeCv(draft.getCvId(), draft.getVersion());

        assertFalse(result.isValid());
        assertEquals(List.of("headline", "experiences", "educations", "skills"),
                result.getErrors().stream().map(error -> error.getField()).toList());
        assertEquals("DRAFT", service.getCv(draft.getCvId()).getStatus());
    }

    @Test
    void finalizeChangesDraftToCompletedWhenComplete() {
        CvDetailDto draft = service.createDraft(new CreateCvDraftRequest());

        CvFinalizeResultDto result = service.finalizeCv(draft.getCvId(), draft.getVersion());

        assertTrue(result.isValid());
        assertEquals("COMPLETED", service.getCv(draft.getCvId()).getStatus());
    }

    @Test
    void setDefaultLeavesExactlyOneDefaultCv() {
        service.setDefault(2L, 1);

        List<Cv> cvs = cvDataStore.getCvsByProfileId(1L);
        assertEquals(1, cvs.stream().filter(Cv::isDefault).count());
        assertTrue(cvs.stream().filter(cv -> cv.getCvId().equals(2L)).findFirst().orElseThrow().isDefault());
    }

    @Test
    void duplicateCopiesDataAsANonDefaultDraft() {
        CvDetailDto duplicate = service.duplicate(1L, 1);
        CvDetailDto original = service.getCv(1L);

        assertNotEquals(original.getCvId(), duplicate.getCvId());
        assertEquals("DRAFT", duplicate.getStatus());
        assertFalse(duplicate.isDefault());
        assertEquals(original.getPersonalInfo().getFirstName(), duplicate.getPersonalInfo().getFirstName());
        duplicate.getPersonalInfo().setFirstName("Changed duplicate");
        assertEquals("John", service.getCv(1L).getPersonalInfo().getFirstName());
    }

    @Test
    void foreignCvIsReportedAsNotFound() {
        Cv foreign = new Cv();
        foreign.setProfileId(999L);
        foreign.setTemplateId(1L);
        foreign.setTemplateCode("CLASSIC");
        foreign.setTemplateName("Classic");
        foreign.setCvName("Foreign CV");
        foreign.setStatus(CvStatus.DRAFT);
        Cv saved = cvDataStore.saveCv(foreign);

        assertThrows(ResourceNotFoundException.class, () -> service.getCv(saved.getCvId()));
    }

    @Test
    void maxCvLimitIsEnforced() {
        properties.setMaxPerProfile(3);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.createDraft(new CreateCvDraftRequest()));
        assertEquals("CV_LIMIT_REACHED", ex.getErrorCode());
    }

    @Test
    void invalidTemplateIsRejectedAndPaginationSizeIsCapped() {
        assertThrows(IllegalArgumentException.class, () -> service.chooseTemplate(3L, 1, "NO_SUCH_TEMPLATE"));
        properties.setMaxPageSize(2);
        assertEquals(2, service.listMyCvs(0, 500).getSize());
        assertEquals(2, service.listMyCvs(0, 500).getContent().size());
    }

    @Test
    void staleVersionConflictsWithoutChangingTheDefault() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.setDefault(2L, 7));
        assertEquals("CV_VERSION_CONFLICT", ex.getErrorCode());
        assertTrue(cvDataStore.getCv(1L).isDefault());
    }

    private JobSeekerProfile profile(boolean complete) {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setProfileId(1L);
        profile.setFirstName("John");
        profile.setLastName("Mwangi");
        profile.setContactEmail("john@example.test");
        profile.setHeadline(complete ? "Marketing Specialist" : null);
        profile.setProfessionalSummary("A professional summary");
        return profile;
    }

    private WorkExperience experience(String company) {
        WorkExperience experience = new WorkExperience();
        experience.setWorkExperienceId(30L);
        experience.setCompanyName(company);
        experience.setJobTitle("Engineer");
        return experience;
    }

    private Education education() {
        Education education = new Education();
        education.setEducationId(40L);
        education.setInstitutionName("University");
        education.setQualification("Degree");
        return education;
    }
}
