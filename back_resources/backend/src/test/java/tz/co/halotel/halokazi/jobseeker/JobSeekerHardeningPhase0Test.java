package tz.co.halotel.halokazi.jobseeker;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tz.co.halotel.halokazi.common.config.CorsConfig;
import tz.co.halotel.halokazi.common.controller.DatabaseTestController;
import tz.co.halotel.halokazi.common.exception.BusinessRuleException;
import tz.co.halotel.halokazi.common.exception.GlobalExceptionHandler;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.controller.*;
import tz.co.halotel.halokazi.jobseeker.dto.*;
import tz.co.halotel.halokazi.jobseeker.model.*;
import tz.co.halotel.halokazi.jobseeker.model.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.repository.CvDataStore;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.*;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/** Red regression tests for the Jobseeker API, request contracts and in-memory store. */
class JobSeekerHardeningPhase0Test {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void profileStoreReturnsDefensiveListsAndDoesNotCreateDataForUnknownProfiles() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        int seeded = store.getEducations(1L).size();
        store.getEducations(1L).clear();
        assertAll(
                () -> assertEquals(seeded, store.getEducations(1L).size()),
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> new EducationService(store, new ProfileStrengthService(store)).getEducations(999L))
        );
    }

    @Test
    void concurrentReadersCannotMutateTheStoredProfileLists() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        int expected = store.getEducations(1L).size();
        int workers = 8;
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(workers);
        try {
            for (int i = 0; i < workers; i++) pool.submit(() -> {
                ready.countDown();
                try { start.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                store.getEducations(1L).clear();
            });
            ready.await(); start.countDown(); pool.shutdown();
            assertTrue(pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS));
            assertEquals(expected, store.getEducations(1L).size());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void putOfUnknownEducationDoesNotCreateAndExistingUpdatePreservesOwnershipAndCreatedAt() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        Education missing = new Education(); missing.setEducationId(90_000L); missing.setProfileId(88L);
        Education original = store.getEducations(1L).getFirst();
        Long id = original.getEducationId();
        var createdAt = original.getCreatedAt();
        Education incoming = new Education(); incoming.setEducationId(id); incoming.setProfileId(999L);
        incoming.setCreatedAt(java.time.LocalDateTime.of(2000, 1, 1, 0, 0));
        incoming.setInstitutionName("Updated institution");
        assertAll(
                () -> assertThrows(ResourceNotFoundException.class, () -> store.saveEducation(1L, missing)),
                () -> {
                    store.saveEducation(1L, incoming);
                    Education stored = store.getEducations(1L).stream().filter(e -> id.equals(e.getEducationId())).findFirst().orElseThrow();
                    assertEquals(1L, stored.getProfileId());
                    assertEquals(createdAt, stored.getCreatedAt());
                }
        );
    }

    @Test
    void generatedIdsAdvancePastExistingClientOrSeedIds() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        Education supplied = new Education(); supplied.setEducationId(11L); supplied.setInstitutionName("Existing id");
        store.saveEducation(1L, supplied);
        Education generated = new Education(); generated.setInstitutionName("Generated id");
        store.saveEducation(1L, generated);
        assertNotEquals(11L, generated.getEducationId());
    }

    @Test
    void savedJobRequiresVacancyAndResourceEqualityIsNullSafe() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        SavedJob invalid = new SavedJob();
        store.getEducations(1L).add(null);
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> store.saveJob(77L, invalid)),
                () -> assertDoesNotThrow(() -> store.deleteEducation(1L, 999L))
        );
    }

    @Test
    void duplicateSkillsAndLanguagesAreConflictsAndDeleteUsesOnlyCatalogId() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileSkill duplicateSkill = new ProfileSkill(null, 1L, 101L, "Duplicate", "ADVANCED", 1.0);
        ProfileLanguage duplicateLanguage = new ProfileLanguage(null, 1L, 1L, "English", "en", "FLUENT");
        int skillsBefore = store.getSkills(1L).size();
        int languagesBefore = store.getLanguages(1L).size();
        assertAll(
                () -> assertThrows(BusinessRuleException.class, () -> store.saveSkill(1L, duplicateSkill)),
                () -> assertThrows(BusinessRuleException.class, () -> store.saveLanguage(1L, duplicateLanguage)),
                () -> assertFalse(store.deleteSkill(1L, 1L)), // row id 1 is not skillId 1
                () -> assertEquals(skillsBefore, store.getSkills(1L).size()),
                () -> assertFalse(store.deleteLanguage(1L, 1L)), // row id 1 is not languageId 1
                () -> assertEquals(languagesBefore, store.getLanguages(1L).size()),
                () -> assertFalse(store.removeSavedJob(1L, 1L)) // vacancy id, not saved-job row id
        );
    }

    @Test
    void profileSeedPersonalContactDataIsRemovedOrDemoGated() {
        JobSeekerProfile profile = new JobSeekerDataStore().getProfile(1L);
        assertAll(
                () -> assertNull(profile.getPhone()),
                () -> assertNull(profile.getContactEmail()),
                () -> assertNull(profile.getAddressLine())
        );
    }

    @Test
    void jobApplicationRequiresOwnedCompletedCvAndRejectsDuplicateVacancy() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        JobApplicationService service = new JobApplicationService(store);
        JobApplicationRequest unknownCv = application(10_000L, 999_999L);
        assertThrows(ResourceNotFoundException.class, () -> service.submitApplication(1L, unknownCv));
    }

    @Test
    void jobApplicationRejectsASecondSubmissionForTheSameVacancy() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        JobApplicationService service = new JobApplicationService(store);
        JobApplicationRequest duplicateVacancy = application(101L, 1L);
        assertThrows(BusinessRuleException.class, () -> service.submitApplication(1L, duplicateVacancy));
    }

    @Test
    void applicationStoresTheSelectedCvTitleRatherThanAConstantFilename() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        JobApplication app = new JobApplicationService(store).submitApplication(1L, application(999L, 2L));
        assertEquals(2L, app.getCvId());
        assertEquals("Digital Marketing Lead — Modern", app.getCvName());
    }

    @Test
    void requestContractsValidatePersonalApplicationAndHeadlineFields() {
        PersonalDetailsUpdateRequest personal = new PersonalDetailsUpdateRequest();
        personal.setFirstName("Asha"); personal.setLastName("Mwakalinga");
        personal.setDateOfBirth(LocalDate.now().plusDays(1)); personal.setGender("Robot");
        personal.setContactEmail("not-an-email"); personal.setPhone("phone-invalid");
        var personalErrors = validator.validate(personal).stream().map(v -> v.getPropertyPath().toString()).toList();
        JobApplicationRequest application = application(20L, 1L);
        application.setRequirementsConfirmed(false); application.setCoverLetter("x".repeat(4001));
        var applicationErrors = validator.validate(application).stream().map(v -> v.getPropertyPath().toString()).toList();

        HeadlineSummaryUpdateRequest empty = new HeadlineSummaryUpdateRequest("   ", null);
        HeadlineSummaryUpdateRequest longSummary = new HeadlineSummaryUpdateRequest("Analyst", "x".repeat(4001));
        assertAll(
                () -> assertTrue(personalErrors.containsAll(List.of("dateOfBirth", "gender", "contactEmail", "phone")), personalErrors.toString()),
                () -> assertTrue(applicationErrors.contains("requirementsConfirmed"), applicationErrors.toString()),
                () -> assertTrue(applicationErrors.contains("coverLetter"), applicationErrors.toString()),
                () -> assertFalse(validator.validate(empty).isEmpty()),
                () -> assertTrue(validator.validate(longSummary).stream().anyMatch(v -> v.getPropertyPath().toString().equals("professionalSummary")))
        );
    }

    @Test
    void applicationConfirmationIsNullableBooleanAndAlertsCanBePatchedWithoutErasingOmittedValues() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        JobAlertPreference current = store.getAlertPreferences(1L);
        JobAlertPreference patch = new JobAlertPreference(); patch.setFrequency("WEEKLY"); patch.setKeywords(null); patch.setPreferredLocations(null); patch.setPreferredCategories(null);
        new JobAlertService(store).updateAlertPreferences(1L, patch);
        JobAlertPreference updated = store.getAlertPreferences(1L);
        assertAll(
                () -> assertEquals(Boolean.class, JobApplicationRequest.class.getDeclaredField("requirementsConfirmed").getType()),
                () -> {
                    for (String name : List.of("enabled", "receiveEmailAlerts", "receiveSmsAlerts"))
                        assertEquals(Boolean.class, JobAlertPreference.class.getDeclaredField(name).getType());
                },
                () -> assertTrue(updated.isEnabled()),
                () -> assertTrue(updated.isReceiveEmailAlerts()),
                () -> assertEquals(current.getKeywords(), updated.getKeywords()),
                () -> assertNotNull(updated.getPreferredLocations()),
                () -> assertNotNull(updated.getPreferredCategories())
        );
    }

    @Test
    void profileVisibilityIsNormalizedAndStrengthForMissingProfileIsNotAZeroSuccess() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        JobSeekerProfileService profiles = new JobSeekerProfileService(store, new ProfileStrengthService(store));
        ProfileVisibilityUpdateRequest lowercase = new ProfileVisibilityUpdateRequest("public");
        assertAll(
                () -> assertEquals("PUBLIC", profiles.updateVisibility(1L, "public").getProfileVisibility()),
                () -> assertFalse(validator.validate(lowercase).stream().anyMatch(v -> v.getPropertyPath().toString().equals("visibility"))),
                () -> assertThrows(ResourceNotFoundException.class, () -> new ProfileStrengthService(store).calculateStrength(999L))
        );
    }

    @Test
    void statsAreCalculatedAndSeededCompletionMatchesStrength() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileStrengthService strength = new ProfileStrengthService(store);
        JobSeekerProfile profile = store.getProfile(1L);
        JobSeekerDashboardStatsDto stats = new JobSeekerProfileService(store, strength).getDashboardStats(1L);
        assertAll(
                () -> assertEquals(strength.calculateStrength(1L).getOverallPercentage(), profile.getCompletionPercentage()),
                () -> assertEquals(1, stats.getActiveJobAlertsCount()),
                () -> assertEquals(new CvDataStore().countCvsByProfileId(1L), stats.getCvsCreatedCount())
        );
    }

    @Test
    void profileChangesRecalculateStrengthAndSharedRecalculationIsNotDuplicatedAcrossServices() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileStrengthService strength = new ProfileStrengthService(store);
        JobSeekerProfileService service = new JobSeekerProfileService(store, strength);
        service.updateHeadlineSummary(1L, new HeadlineSummaryUpdateRequest("A new headline", "A much longer professional summary for testing recalculation."));
        long duplicated = List.of(EducationService.class, WorkExperienceService.class, SkillService.class, CertificationService.class, LanguageService.class)
                .stream().flatMap(type -> java.util.Arrays.stream(type.getDeclaredMethods()))
                .filter(method -> method.getName().equals("updateProfileCompletion")).count();
        assertAll(
                () -> assertEquals(strength.calculateStrength(1L).getOverallPercentage(), store.getProfile(1L).getCompletionPercentage()),
                () -> assertEquals(0, duplicated)
        );
    }

    @Test
    void profileDetailCalculatesStrengthOnlyOnceAndProfileControllersDoNotReachIntoTheStore() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileStrengthService strength = org.mockito.Mockito.spy(new ProfileStrengthService(store));
        new JobSeekerProfileService(store, strength).getProfileDetail(1L);
        org.mockito.Mockito.verify(strength, org.mockito.Mockito.times(1)).calculateStrength(1L);
        assertFalse(java.util.Arrays.stream(JobSeekerProfileController.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == JobSeekerDataStore.class));
    }

    @Test
    void deletingACvUsedByAnApplicationReturnsConflict() {
        JobSeekerDataStore profiles = new JobSeekerDataStore();
        CvDataStore cvs = new CvDataStore();
        CvService service = new CvService(cvs, profiles, new CurrentJobSeekerProvider(), new tz.co.halotel.halokazi.jobseeker.mapper.CvMapper(),
                new tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper(), new tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties());
        assertThrows(BusinessRuleException.class, () -> service.delete(1L, cvs.getCv(1L).getVersion()));
    }

    @Test
    void applicationDefaultsToTheCurrentCompletedDefaultCvAndUsesCvStore() {
        assertTrue(java.util.Arrays.stream(JobApplicationService.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == CvDataStore.class));
        assertTrue(java.util.Arrays.stream(JobSeekerProfileService.class.getDeclaredFields())
                .anyMatch(field -> field.getType() == CvDataStore.class));
    }

    @Test
    void invalidContentTypeForCvRequestReturns415RatherThan500() throws Exception {
        JobSeekerDataStore profiles = new JobSeekerDataStore();
        CvService service = new CvService(new CvDataStore(), profiles, new CurrentJobSeekerProvider(),
                new tz.co.halotel.halokazi.jobseeker.mapper.CvMapper(), new tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper(),
                new tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties());
        var validatorFactory = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean(); validatorFactory.afterPropertiesSet();
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new CvController(service, new tz.co.halotel.halokazi.jobseeker.service.CvPdfRenderer()))
                .setValidator(validatorFactory).setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/cvs/3/title")
                        .header("If-Match", "1").contentType("text/plain").content("{\"title\":\"Updated\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnsupportedMediaType())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false));
    }

    @Test
    void cvMissingBodyMalformedJsonBadDateAndMissingIfMatchReturnFourHundred() throws Exception {
        JobSeekerDataStore profiles = new JobSeekerDataStore();
        CvService service = new CvService(new CvDataStore(), profiles, new CurrentJobSeekerProvider(),
                new tz.co.halotel.halokazi.jobseeker.mapper.CvMapper(), new tz.co.halotel.halokazi.jobseeker.mapper.CvPrefillMapper(),
                new tz.co.halotel.halokazi.jobseeker.config.CvConfigProperties());
        var validatorFactory = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean(); validatorFactory.afterPropertiesSet();
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new CvController(service, new tz.co.halotel.halokazi.jobseeker.service.CvPdfRenderer()))
                .setValidator(validatorFactory)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        assertAll(
                () -> mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/cvs/3/personal-info").contentType("application/json"))
                        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest()),
                () -> mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/cvs/3/title").header("If-Match", "1").contentType("application/json").content("{"))
                        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest()),
                () -> mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/cvs/3/experience").header("If-Match", "1").contentType("application/json").content("[{\"companyName\":\"Example\",\"jobTitle\":\"Analyst\",\"startDate\":\"not-a-date\"}]"))
                        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest()),
                () -> mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/cvs/3/title").contentType("application/json").content("{\"title\":\"Updated\"}"))
                        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest()),
                () -> mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/cvs/3/title").header("If-Match", "not-a-version").contentType("application/json").content("{\"title\":\"Updated\"}"))
                        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
        );
    }

    @Test
    void nonCvMissingBodyReturnsApi400Not500() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        var controller = new JobSeekerEducationController(new EducationService(store, new ProfileStrengthService(store)));
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/jobseeker/education?profileId=1")
                        .contentType("application/json"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false));
    }

    @Test
    void invalidEnumLikeVisibilityValueReturnsWrapped400() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileStrengthService strength = new ProfileStrengthService(store);
        var validator = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        var controller = new JobSeekerProfileController(new JobSeekerProfileService(store, strength), strength, store);
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/jobseeker/profile/visibility")
                        .contentType("application/json").content("{\"visibility\":\"Robot\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.visibility").exists());
    }

    @Test
    void unsupportedHttpMethodReturnsWrapped405() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        ProfileStrengthService strength = new ProfileStrengthService(store);
        var controller = new JobSeekerProfileController(new JobSeekerProfileService(store, strength), strength, store);
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/jobseeker/profile"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false));
    }

    @Test
    void postApplicationResponseCodeRemainsTheFrontendCompatible200() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(
                        new JobSeekerApplicationController(new JobApplicationService(store)))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/jobseeker/applications?profileId=1")
                        .contentType("application/json")
                        .content("{\"vacancyId\":999,\"cvId\":1,\"coverLetter\":\"Please consider my application.\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    void currentProfileProviderResolvesRequestedProfileAndProfilePictureUsesValidatedDto() throws Exception {
        boolean pictureUsesMap = java.util.Arrays.stream(JobSeekerProfileController.class.getDeclaredMethods())
                .filter(method -> method.getName().equals("updatePicture"))
                .flatMap(method -> java.util.Arrays.stream(method.getParameters()))
                .filter(parameter -> parameter.isAnnotationPresent(RequestBody.class))
                .anyMatch(parameter -> parameter.getType() == java.util.Map.class);
        assertAll(
                () -> assertDoesNotThrow(() -> CurrentJobSeekerProvider.class.getMethod("resolve", Long.class)),
                () -> assertFalse(pictureUsesMap)
        );
    }

    @Test
    void requestBodiesUseValidatedRequestDtosInsteadOfMutableModels() {
        List<Class<?>> controllers = List.of(JobSeekerEducationController.class, JobSeekerWorkExperienceController.class,
                JobSeekerCertificationController.class, JobSeekerSkillController.class, JobSeekerLanguageController.class,
                JobSeekerProjectController.class, JobSeekerReferenceController.class, JobSeekerSavedJobController.class,
                JobSeekerAlertController.class);
        List<String> violations = new ArrayList<>();
        for (Class<?> controller : controllers) for (var method : controller.getDeclaredMethods()) for (Parameter parameter : method.getParameters()) {
            if (!parameter.isAnnotationPresent(RequestBody.class)) continue;
            if (parameter.getType().getPackageName().equals("tz.co.halotel.halokazi.jobseeker.model")) violations.add(controller.getSimpleName()+"."+method.getName()+" uses model "+parameter.getType().getSimpleName());
            if (!parameter.isAnnotationPresent(jakarta.validation.Valid.class)) violations.add(controller.getSimpleName()+"."+method.getName()+" is missing @Valid");
        }
        assertTrue(violations.isEmpty(), String.join("; ", violations));
    }

    @Test
    void crossOriginIsConfiguredGloballyAndErrorAdviceIsShared() {
        List<Class<?>> controllers = List.of(DatabaseTestController.class, JobSeekerAlertController.class, JobSeekerApplicationController.class,
                JobSeekerCertificationController.class, JobSeekerEducationController.class, JobSeekerLanguageController.class,
                JobSeekerProfileController.class, JobSeekerProjectController.class, JobSeekerReferenceController.class,
                JobSeekerSavedJobController.class, JobSeekerSkillController.class, JobSeekerWorkExperienceController.class);
        long localCorsAnnotations = controllers.stream().filter(type -> type.isAnnotationPresent(CrossOrigin.class)).count();
        var scanner = new org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new org.springframework.core.type.filter.AnnotationTypeFilter(RestControllerAdvice.class));
        List<String> adviceClasses = scanner.findCandidateComponents("tz.co.halotel.halokazi").stream()
                .map(org.springframework.beans.factory.config.BeanDefinition::getBeanClassName).sorted().toList();
        var cors = new CorsConfig("http://localhost:5173, https://frontend.example.test").corsConfiguration();
        assertAll(
                () -> assertEquals(0, localCorsAnnotations),
                () -> assertEquals(List.of(GlobalExceptionHandler.class.getName()), adviceClasses),
                () -> assertTrue(GlobalExceptionHandler.class.isAnnotationPresent(RestControllerAdvice.class)),
                () -> assertEquals(List.of("http://localhost:5173", "https://frontend.example.test"), cors.getAllowedOrigins()),
                () -> assertTrue(cors.getAllowedMethods().containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"))),
                () -> assertTrue(cors.getAllowedHeaders().contains("If-Match")),
                () -> assertEquals(List.of("Content-Disposition", "ETag"), cors.getExposedHeaders())
        );
    }

    @Test
    void requestedProfileIdsAreSupportedOnEveryProfileSubresource() {
        List<String> missing = new ArrayList<>();
        for (String methodName : List.of("updateHeadlineSummary", "updateVisibility", "updatePicture", "getProfileStrength", "getStats", "getRecommendations")) {
            boolean acceptsId = java.util.Arrays.stream(JobSeekerProfileController.class.getDeclaredMethods())
                    .filter(method -> method.getName().equals(methodName))
                    .flatMap(method -> java.util.Arrays.stream(method.getParameters()))
                    .anyMatch(parameter -> parameter.isAnnotationPresent(org.springframework.web.bind.annotation.RequestParam.class)
                            && parameter.getName().equals("profileId"));
            if (!acceptsId) missing.add(methodName);
        }
        assertTrue(missing.isEmpty(), "Missing requested profileId binding: " + missing);
    }

    @Test
    void educationCreateIgnoresClientIdAndUpdateMissingIdReturnsWrapped404() throws Exception {
        JobSeekerDataStore store = new JobSeekerDataStore();
        var educationService = new EducationService(store, new ProfileStrengthService(store));
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new JobSeekerEducationController(educationService))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        var created = mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/jobseeker/education?profileId=1")
                        .contentType("application/json")
                        .content("{\"educationId\":99001,\"institutionName\":\"New University\",\"qualification\":\"Degree\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andReturn();
        String response = created.getResponse().getContentAsString();
        assertFalse(response.contains("\"educationId\":99001"));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/jobseeker/education/99002?profileId=1")
                        .contentType("application/json").content("{\"institutionName\":\"Missing row\",\"qualification\":\"Degree\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false));
    }

    @Test
    void genericErrorResponseDoesNotExposeExceptionText() {
        var response = new GlobalExceptionHandler().handleGeneralException(new IllegalStateException("private database detail"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getMessage().contains("private database detail"));
    }

    @Test
    void missingProfileDeleteReturnsApiResponseNotAnEmptyNotFound() {
        JobSeekerDataStore store = new JobSeekerDataStore();
        var service = new EducationService(store, new ProfileStrengthService(store));
        var validator = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new JobSeekerEducationController(service))
                .setValidator(validator).setControllerAdvice(new GlobalExceptionHandler()).build();
        assertDoesNotThrow(() -> mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/jobseeker/education/99999?profileId=1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(false)));
    }

    private JobApplicationRequest application(Long vacancyId, Long cvId) {
        JobApplicationRequest request = new JobApplicationRequest(); request.setVacancyId(vacancyId); request.setCvId(cvId); request.setCoverLetter("Please consider my application."); return request;
    }

}
