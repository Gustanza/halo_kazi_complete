package tz.co.halotel.halokazi.jobseeker.repository;

import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.jobseeker.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class JobSeekerDataStore {

    public static final Long DEFAULT_PROFILE_ID = 1L;
    public static final Long DEFAULT_USER_ID = 101L;

    private final Map<Long, JobSeekerProfile> profiles = new ConcurrentHashMap<>();
    private final Map<Long, List<Education>> educations = new ConcurrentHashMap<>();
    private final Map<Long, List<WorkExperience>> workExperiences = new ConcurrentHashMap<>();
    private final Map<Long, List<ProfileSkill>> skills = new ConcurrentHashMap<>();
    private final Map<Long, List<Certification>> certifications = new ConcurrentHashMap<>();
    private final Map<Long, List<ProfileLanguage>> languages = new ConcurrentHashMap<>();
    private final Map<Long, List<ProfileProject>> projects = new ConcurrentHashMap<>();
    private final Map<Long, List<ProfileReference>> references = new ConcurrentHashMap<>();
    private final Map<Long, List<SavedJob>> savedJobs = new ConcurrentHashMap<>();
    private final Map<Long, List<JobApplication>> applications = new ConcurrentHashMap<>();
    private final Map<Long, JobAlertPreference> alertPreferences = new ConcurrentHashMap<>();
    private final List<RecommendedJob> recommendedJobsPool = new ArrayList<>();

    private final AtomicLong educationIdSeq = new AtomicLong(10);
    private final AtomicLong workExpIdSeq = new AtomicLong(10);
    private final AtomicLong skillIdSeq = new AtomicLong(10);
    private final AtomicLong certIdSeq = new AtomicLong(10);
    private final AtomicLong langIdSeq = new AtomicLong(10);
    private final AtomicLong projIdSeq = new AtomicLong(10);
    private final AtomicLong refIdSeq = new AtomicLong(10);
    private final AtomicLong savedJobIdSeq = new AtomicLong(10);
    private final AtomicLong appIdSeq = new AtomicLong(10);

    public JobSeekerDataStore() {
        seedInitialData();
    }

    private void seedInitialData() {
        // Seed John Mwangi's profile matching the user's frontend screenshot
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setProfileId(DEFAULT_PROFILE_ID);
        profile.setUserId(DEFAULT_USER_ID);
        profile.setFirstName("John");
        profile.setLastName("Mwangi");
        profile.setHeadline("Marketing Specialist");
        profile.setProfessionalSummary("Results-driven marketing professional with 5+ years of experience in digital marketing, brand strategy and social media management");
        profile.setContactEmail("john.mwangi@halokazi.co.tz");
        profile.setPhone("+255 712 345 678");
        profile.setAlternatePhone("+255 765 987 321");
        profile.setPhoneVerified(true);
        profile.setDateOfBirth(LocalDate.of(1992, 5, 12));
        profile.setGender("Male");
        profile.setNationality("Tanzania");
        profile.setRegionId(7L);
        profile.setRegionName("Dar es Salaam");
        profile.setDistrictId(1L);
        profile.setDistrictName("Ilala");
        profile.setCurrentLocation("Dar es Salaam, Tanzania");
        profile.setAddressLine("Samora Avenue, Posta Mpya");
        profile.setProfilePictureUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80");
        profile.setProfilePictureFileId(501L);
        profile.setProfileVisibility("Public");
        profile.setExpectedSalaryMin(new BigDecimal("2500000.00"));
        profile.setReceiveJobAlerts(true);
        profile.setCompletionPercentage(80.0);
        profile.setMemberSince("July 2024");
        profile.setLastUpdated("3 Sept 2026");
        profile.setCompletionUpdatedAt(LocalDateTime.now().minusDays(2));
        profile.setCreatedAt(LocalDateTime.of(2024, 7, 15, 10, 0));
        profile.setUpdatedAt(LocalDateTime.of(2026, 9, 3, 14, 30));

        profiles.put(DEFAULT_PROFILE_ID, profile);

        // Seed Education
        List<Education> eduList = new ArrayList<>();
        Education edu1 = new Education();
        edu1.setEducationId(1L);
        edu1.setProfileId(DEFAULT_PROFILE_ID);
        edu1.setEducationLevelId(4L);
        edu1.setEducationLevelName("Bachelor Degree");
        edu1.setInstitutionName("University of Dar es Salaam (UDSM)");
        edu1.setQualification("Bachelor of Commerce in Marketing");
        edu1.setFieldOfStudy("Marketing & Business Administration");
        edu1.setGrade("Upper Second Class (GPA 4.1)");
        edu1.setStartDate(LocalDate.of(2011, 10, 1));
        edu1.setEndDate(LocalDate.of(2014, 7, 30));
        edu1.setCurrent(false);
        edu1.setCreatedAt(LocalDateTime.now().minusMonths(6));
        edu1.setUpdatedAt(LocalDateTime.now().minusMonths(6));
        eduList.add(edu1);
        educations.put(DEFAULT_PROFILE_ID, eduList);

        // Seed Work Experience
        List<WorkExperience> workList = new ArrayList<>();
        WorkExperience work1 = new WorkExperience();
        work1.setWorkExperienceId(1L);
        work1.setProfileId(DEFAULT_PROFILE_ID);
        work1.setCompanyName("Halotel Tanzania PLC");
        work1.setJobTitle("Digital Marketing Lead");
        work1.setEmploymentTypeId(1L);
        work1.setEmploymentTypeName("Full Time");
        work1.setLocation("Dar es Salaam, Tanzania");
        work1.setDescription("Leading digital campaign strategies, influencer partnerships, paid search & performance marketing for Halokazi products.");
        work1.setStartDate(LocalDate.of(2021, 3, 1));
        work1.setCurrent(true);
        work1.setCreatedAt(LocalDateTime.now().minusMonths(12));
        work1.setUpdatedAt(LocalDateTime.now().minusMonths(2));
        workList.add(work1);

        WorkExperience work2 = new WorkExperience();
        work2.setWorkExperienceId(2L);
        work2.setProfileId(DEFAULT_PROFILE_ID);
        work2.setCompanyName("Vodacom Tanzania PLC");
        work2.setJobTitle("Social Media & Brand Strategist");
        work2.setEmploymentTypeId(1L);
        work2.setEmploymentTypeName("Full Time");
        work2.setLocation("Dar es Salaam, Tanzania");
        work2.setDescription("Managed multi-channel brand campaigns, customer engagement funnels, and data analytics dashboards.");
        work2.setStartDate(LocalDate.of(2018, 1, 15));
        work2.setEndDate(LocalDate.of(2021, 2, 28));
        work2.setCurrent(false);
        work2.setCreatedAt(LocalDateTime.now().minusMonths(24));
        work2.setUpdatedAt(LocalDateTime.now().minusMonths(24));
        workList.add(work2);
        workExperiences.put(DEFAULT_PROFILE_ID, workList);

        // Seed Skills
        List<ProfileSkill> skillList = new ArrayList<>();
        skillList.add(new ProfileSkill(1L, DEFAULT_PROFILE_ID, 101L, "Digital Marketing Strategy", "EXPERT", 6.0));
        skillList.add(new ProfileSkill(2L, DEFAULT_PROFILE_ID, 102L, "Social Media Management", "ADVANCED", 5.0));
        skillList.add(new ProfileSkill(3L, DEFAULT_PROFILE_ID, 103L, "Google Analytics & SEO", "ADVANCED", 4.5));
        skillList.add(new ProfileSkill(4L, DEFAULT_PROFILE_ID, 104L, "Brand Strategy & Positioning", "EXPERT", 5.5));
        skillList.add(new ProfileSkill(5L, DEFAULT_PROFILE_ID, 105L, "Content Creation & Copywriting", "INTERMEDIATE", 3.0));
        skills.put(DEFAULT_PROFILE_ID, skillList);

        // Seed Certifications
        List<Certification> certList = new ArrayList<>();
        Certification cert1 = new Certification();
        cert1.setCertificationId(1L);
        cert1.setProfileId(DEFAULT_PROFILE_ID);
        cert1.setCertificationName("Google Ads Certified Search Specialist");
        cert1.setIssuingOrganization("Google Digital Academy");
        cert1.setCredentialId("GA-2023-88741");
        cert1.setIssueDate(LocalDate.of(2023, 6, 1));
        cert1.setExpiryDate(LocalDate.of(2026, 6, 1));
        cert1.setCertificateUrl("https://skillshop.credential.net/google-ads-certified");
        cert1.setCreatedAt(LocalDateTime.now().minusMonths(8));
        cert1.setUpdatedAt(LocalDateTime.now().minusMonths(8));
        certList.add(cert1);

        Certification cert2 = new Certification();
        cert2.setCertificationId(2L);
        cert2.setProfileId(DEFAULT_PROFILE_ID);
        cert2.setCertificationName("HubSpot Inbound Marketing Professional");
        cert2.setIssuingOrganization("HubSpot Academy");
        cert2.setCredentialId("HS-INB-99231");
        cert2.setIssueDate(LocalDate.of(2024, 2, 10));
        cert2.setExpiryDate(LocalDate.of(2027, 2, 10));
        cert2.setCertificateUrl("https://academy.hubspot.com/certificates/inbound");
        cert2.setCreatedAt(LocalDateTime.now().minusMonths(5));
        cert2.setUpdatedAt(LocalDateTime.now().minusMonths(5));
        certList.add(cert2);
        certifications.put(DEFAULT_PROFILE_ID, certList);

        // Seed Languages
        List<ProfileLanguage> langList = new ArrayList<>();
        langList.add(new ProfileLanguage(1L, DEFAULT_PROFILE_ID, 1L, "English", "en", "FLUENT"));
        langList.add(new ProfileLanguage(2L, DEFAULT_PROFILE_ID, 2L, "Swahili", "sw", "NATIVE"));
        languages.put(DEFAULT_PROFILE_ID, langList);

        // Seed Projects
        List<ProfileProject> projList = new ArrayList<>();
        ProfileProject proj1 = new ProfileProject();
        proj1.setProjectId(1L);
        proj1.setProfileId(DEFAULT_PROFILE_ID);
        proj1.setTitle("National Halokazi Launch Campaign");
        proj1.setRole("Campaign Director");
        proj1.setDescription("Executed a nationwide digital rollout campaign across 15 regions of Tanzania, resulting in 500k+ downloads.");
        proj1.setProjectUrl("https://halokazi.co.tz/campaigns/launch");
        proj1.setStartDate(LocalDate.of(2025, 1, 10));
        proj1.setEndDate(LocalDate.of(2025, 6, 30));
        proj1.setSortOrder(1);
        proj1.setCreatedAt(LocalDateTime.now().minusMonths(4));
        proj1.setUpdatedAt(LocalDateTime.now().minusMonths(4));
        projList.add(proj1);
        projects.put(DEFAULT_PROFILE_ID, projList);

        // Seed References
        List<ProfileReference> refList = new ArrayList<>();
        ProfileReference ref1 = new ProfileReference();
        ref1.setReferenceId(1L);
        ref1.setProfileId(DEFAULT_PROFILE_ID);
        ref1.setFullName("Amina Bakari");
        ref1.setJobTitle("Head of Marketing");
        ref1.setOrganization("Vodacom Tanzania PLC");
        ref1.setRelationship("Former Supervisor");
        ref1.setEmail("amina.bakari@vodacom.co.tz");
        ref1.setPhone("+255 754 112 233");
        ref1.setSortOrder(1);
        ref1.setCreatedAt(LocalDateTime.now().minusMonths(6));
        ref1.setUpdatedAt(LocalDateTime.now().minusMonths(6));
        refList.add(ref1);
        references.put(DEFAULT_PROFILE_ID, refList);

        // Seed Saved Jobs (4 saved jobs)
        List<SavedJob> savedList = new ArrayList<>();
        savedList.add(new SavedJob(1L, DEFAULT_PROFILE_ID, 101L, "Digital Marketing Specialist", "Halotel PLC", null, "Dar es Salaam", "Full Time", "TZS 2.5M - 3.5M", "15 Nov 2026"));
        savedList.add(new SavedJob(2L, DEFAULT_PROFILE_ID, 102L, "Relationship Manager", "NMB Bank PLC", null, "Dar es Salaam", "Full Time", "TZS 3.0M - 4.5M", "20 Nov 2026"));
        savedList.add(new SavedJob(3L, DEFAULT_PROFILE_ID, 103L, "Communications Officer", "CRDB Bank", null, "Arusha", "Full Time", "TZS 2.2M - 3.2M", "30 Nov 2026"));
        savedList.add(new SavedJob(4L, DEFAULT_PROFILE_ID, 104L, "Growth Marketing Lead", "Kigali Tech Hub", null, "Remote / Dar", "Contract", "TZS 4.0M - 6.0M", "10 Dec 2026"));
        savedJobs.put(DEFAULT_PROFILE_ID, savedList);

        // Seed Applications (7 applications matching stats)
        List<JobApplication> appList = new ArrayList<>();
        JobApplication app1 = new JobApplication();
        app1.setApplicationId(1L);
        app1.setApplicationRef("HK-2026-000101");
        app1.setVacancyId(101L);
        app1.setJobTitle("Digital Marketing Specialist");
        app1.setCompanyName("Halotel PLC");
        app1.setLocation("Dar es Salaam");
        app1.setProfileId(DEFAULT_PROFILE_ID);
        app1.setCvId(1L);
        app1.setCvName("John_Mwangi_Marketing_CV_2026.pdf");
        app1.setCoverLetter("I am writing to express my strong interest in the Digital Marketing Specialist position...");
        app1.setStatus("IN_REVIEW");
        app1.setStatusRemarks("Application is currently under technical review by HR team.");
        app1.setProfileCompletionAtSubmit(80.0);
        app1.setSubmittedAt(LocalDateTime.now().minusDays(5));
        app1.setUpdatedAt(LocalDateTime.now().minusDays(1));
        appList.add(app1);

        JobApplication app2 = new JobApplication();
        app2.setApplicationId(2L);
        app2.setApplicationRef("HK-2026-000102");
        app2.setVacancyId(102L);
        app2.setJobTitle("Relationship Manager");
        app2.setCompanyName("NMB Bank PLC");
        app2.setLocation("Dar es Salaam");
        app2.setProfileId(DEFAULT_PROFILE_ID);
        app2.setCvId(1L);
        app2.setCvName("John_Mwangi_Marketing_CV_2026.pdf");
        app2.setCoverLetter("With over 5 years in client engagement and brand positioning, I am eager to contribute...");
        app2.setStatus("SHORTLISTED");
        app2.setStatusRemarks("Congratulations! You have been shortlisted for an interview.");
        app2.setProfileCompletionAtSubmit(80.0);
        app2.setSubmittedAt(LocalDateTime.now().minusDays(12));
        app2.setUpdatedAt(LocalDateTime.now().minusDays(2));
        appList.add(app2);

        // 5 more applications to match the count 7
        for (int i = 3; i <= 7; i++) {
            JobApplication app = new JobApplication();
            app.setApplicationId((long) i);
            app.setApplicationRef("HK-2026-00010" + i);
            app.setVacancyId((long) (200 + i));
            app.setJobTitle(i % 2 == 0 ? "Brand Communications Officer" : "Social Media Manager");
            app.setCompanyName(i % 2 == 0 ? "Tigo Tanzania" : "Airtel Tanzania");
            app.setLocation("Dar es Salaam");
            app.setProfileId(DEFAULT_PROFILE_ID);
            app.setCvId(1L);
            app.setCvName("John_Mwangi_Marketing_CV_2026.pdf");
            app.setCoverLetter("Please accept my application for this exciting vacancy...");
            app.setStatus(i == 3 ? "SUBMITTED" : (i == 4 ? "IN_REVIEW" : "SUBMITTED"));
            app.setProfileCompletionAtSubmit(80.0);
            app.setSubmittedAt(LocalDateTime.now().minusDays(15 + i));
            app.setUpdatedAt(LocalDateTime.now().minusDays(15 + i));
            appList.add(app);
        }
        applications.put(DEFAULT_PROFILE_ID, appList);

        // Seed Alert Preferences (2 active alerts)
        JobAlertPreference alertPref = new JobAlertPreference();
        alertPref.setProfileId(DEFAULT_PROFILE_ID);
        alertPref.setEnabled(true);
        alertPref.setReceiveEmailAlerts(true);
        alertPref.setReceiveSmsAlerts(true);
        alertPref.setFrequency("DAILY");
        alertPref.getKeywords().addAll(Arrays.asList("Digital Marketing", "Brand Specialist", "SEO"));
        alertPref.getPreferredLocations().addAll(Arrays.asList("Dar es Salaam", "Zanzibar"));
        alertPref.getPreferredCategories().addAll(Arrays.asList("Marketing & Advertising", "Telecommunications"));
        alertPref.setMinSalary(new BigDecimal("2000000.00"));
        alertPreferences.put(DEFAULT_PROFILE_ID, alertPref);

        // Seed Recommended Jobs matching dashboard.js
        recommendedJobsPool.add(new RecommendedJob(1L, "Digital Marketing Specialist", "Halotel PLC", "HLT", "#FD5C20", "Dar es Salaam", "Full Time", "TZS 2.5M - 3.5M", 95, true));
        recommendedJobsPool.add(new RecommendedJob(2L, "Relationship Manager", "NMB Bank PLC", "NMB", "#F6861F", "Dar es Salaam", "Full Time", "TZS 3.0M - 4.5M", 88, true));
        recommendedJobsPool.add(new RecommendedJob(3L, "Customer Care Agent", "Vodacom Tanzania", "VOD", "#E3001B", "Dar es Salaam", "Full Time", "TZS 1.5M - 2.2M", 82, true));
        recommendedJobsPool.add(new RecommendedJob(4L, "Content Marketing Strategist", "Airtel Tanzania", "AIR", "#E40000", "Dar es Salaam", "Full Time", "TZS 2.8M - 3.8M", 90, false));
    }

    public JobSeekerProfile getProfile(Long profileId) {
        return profiles.get(profileId);
    }

    public void saveProfile(JobSeekerProfile profile) {
        profiles.put(profile.getProfileId(), profile);
    }

    public List<Education> getEducations(Long profileId) {
        return educations.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public Education saveEducation(Long profileId, Education edu) {
        List<Education> list = getEducations(profileId);
        if (edu.getEducationId() == null) {
            edu.setEducationId(educationIdSeq.incrementAndGet());
            edu.setProfileId(profileId);
            edu.setCreatedAt(LocalDateTime.now());
            edu.setUpdatedAt(LocalDateTime.now());
            list.add(edu);
        } else {
            edu.setUpdatedAt(LocalDateTime.now());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getEducationId().equals(edu.getEducationId())) {
                    list.set(i, edu);
                    return edu;
                }
            }
            list.add(edu);
        }
        return edu;
    }

    public boolean deleteEducation(Long profileId, Long educationId) {
        List<Education> list = getEducations(profileId);
        return list.removeIf(e -> e.getEducationId().equals(educationId));
    }

    public List<WorkExperience> getWorkExperiences(Long profileId) {
        return workExperiences.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public WorkExperience saveWorkExperience(Long profileId, WorkExperience exp) {
        List<WorkExperience> list = getWorkExperiences(profileId);
        if (exp.getWorkExperienceId() == null) {
            exp.setWorkExperienceId(workExpIdSeq.incrementAndGet());
            exp.setProfileId(profileId);
            exp.setCreatedAt(LocalDateTime.now());
            exp.setUpdatedAt(LocalDateTime.now());
            list.add(exp);
        } else {
            exp.setUpdatedAt(LocalDateTime.now());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWorkExperienceId().equals(exp.getWorkExperienceId())) {
                    list.set(i, exp);
                    return exp;
                }
            }
            list.add(exp);
        }
        return exp;
    }

    public boolean deleteWorkExperience(Long profileId, Long workExpId) {
        List<WorkExperience> list = getWorkExperiences(profileId);
        return list.removeIf(e -> e.getWorkExperienceId().equals(workExpId));
    }

    public List<ProfileSkill> getSkills(Long profileId) {
        return skills.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public ProfileSkill saveSkill(Long profileId, ProfileSkill skill) {
        List<ProfileSkill> list = getSkills(profileId);
        if (skill.getId() == null) {
            skill.setId(skillIdSeq.incrementAndGet());
            skill.setProfileId(profileId);
            skill.setCreatedAt(LocalDateTime.now());
            list.add(skill);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(skill.getId())) {
                    list.set(i, skill);
                    return skill;
                }
            }
            list.add(skill);
        }
        return skill;
    }

    public boolean deleteSkill(Long profileId, Long skillId) {
        List<ProfileSkill> list = getSkills(profileId);
        return list.removeIf(s -> s.getId().equals(skillId) || (s.getSkillId() != null && s.getSkillId().equals(skillId)));
    }

    public List<Certification> getCertifications(Long profileId) {
        return certifications.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public Certification saveCertification(Long profileId, Certification cert) {
        List<Certification> list = getCertifications(profileId);
        if (cert.getCertificationId() == null) {
            cert.setCertificationId(certIdSeq.incrementAndGet());
            cert.setProfileId(profileId);
            cert.setCreatedAt(LocalDateTime.now());
            cert.setUpdatedAt(LocalDateTime.now());
            list.add(cert);
        } else {
            cert.setUpdatedAt(LocalDateTime.now());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCertificationId().equals(cert.getCertificationId())) {
                    list.set(i, cert);
                    return cert;
                }
            }
            list.add(cert);
        }
        return cert;
    }

    public boolean deleteCertification(Long profileId, Long certId) {
        List<Certification> list = getCertifications(profileId);
        return list.removeIf(c -> c.getCertificationId().equals(certId));
    }

    public List<ProfileLanguage> getLanguages(Long profileId) {
        return languages.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public ProfileLanguage saveLanguage(Long profileId, ProfileLanguage lang) {
        List<ProfileLanguage> list = getLanguages(profileId);
        if (lang.getId() == null) {
            lang.setId(langIdSeq.incrementAndGet());
            lang.setProfileId(profileId);
            list.add(lang);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(lang.getId())) {
                    list.set(i, lang);
                    return lang;
                }
            }
            list.add(lang);
        }
        return lang;
    }

    public boolean deleteLanguage(Long profileId, Long langId) {
        List<ProfileLanguage> list = getLanguages(profileId);
        return list.removeIf(l -> l.getId().equals(langId) || (l.getLanguageId() != null && l.getLanguageId().equals(langId)));
    }

    public List<ProfileProject> getProjects(Long profileId) {
        return projects.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public ProfileProject saveProject(Long profileId, ProfileProject proj) {
        List<ProfileProject> list = getProjects(profileId);
        if (proj.getProjectId() == null) {
            proj.setProjectId(projIdSeq.incrementAndGet());
            proj.setProfileId(profileId);
            proj.setCreatedAt(LocalDateTime.now());
            proj.setUpdatedAt(LocalDateTime.now());
            list.add(proj);
        } else {
            proj.setUpdatedAt(LocalDateTime.now());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getProjectId().equals(proj.getProjectId())) {
                    list.set(i, proj);
                    return proj;
                }
            }
            list.add(proj);
        }
        return proj;
    }

    public boolean deleteProject(Long profileId, Long projectId) {
        List<ProfileProject> list = getProjects(profileId);
        return list.removeIf(p -> p.getProjectId().equals(projectId));
    }

    public List<ProfileReference> getReferences(Long profileId) {
        return references.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public ProfileReference saveReference(Long profileId, ProfileReference ref) {
        List<ProfileReference> list = getReferences(profileId);
        if (ref.getReferenceId() == null) {
            ref.setReferenceId(refIdSeq.incrementAndGet());
            ref.setProfileId(profileId);
            ref.setCreatedAt(LocalDateTime.now());
            ref.setUpdatedAt(LocalDateTime.now());
            list.add(ref);
        } else {
            ref.setUpdatedAt(LocalDateTime.now());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getReferenceId().equals(ref.getReferenceId())) {
                    list.set(i, ref);
                    return ref;
                }
            }
            list.add(ref);
        }
        return ref;
    }

    public boolean deleteReference(Long profileId, Long referenceId) {
        List<ProfileReference> list = getReferences(profileId);
        return list.removeIf(r -> r.getReferenceId().equals(referenceId));
    }

    public List<SavedJob> getSavedJobs(Long profileId) {
        return savedJobs.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public SavedJob saveJob(Long profileId, SavedJob job) {
        List<SavedJob> list = getSavedJobs(profileId);
        for (SavedJob existing : list) {
            if (existing.getVacancyId().equals(job.getVacancyId())) {
                return existing;
            }
        }
        if (job.getId() == null) {
            job.setId(savedJobIdSeq.incrementAndGet());
        }
        job.setProfileId(profileId);
        job.setSavedAt(LocalDateTime.now());
        list.add(job);
        return job;
    }

    public boolean removeSavedJob(Long profileId, Long vacancyId) {
        List<SavedJob> list = getSavedJobs(profileId);
        return list.removeIf(j -> j.getVacancyId().equals(vacancyId) || j.getId().equals(vacancyId));
    }

    public List<JobApplication> getApplications(Long profileId) {
        return applications.computeIfAbsent(profileId, k -> new ArrayList<>());
    }

    public JobApplication saveApplication(Long profileId, JobApplication app) {
        List<JobApplication> list = getApplications(profileId);
        if (app.getApplicationId() == null) {
            app.setApplicationId(appIdSeq.incrementAndGet());
            app.setApplicationRef("HK-2026-" + String.format("%06d", app.getApplicationId()));
            app.setProfileId(profileId);
            app.setStatus("SUBMITTED");
            app.setSubmittedAt(LocalDateTime.now());
            app.setUpdatedAt(LocalDateTime.now());
            list.add(app);
        } else {
            app.setUpdatedAt(LocalDateTime.now());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getApplicationId().equals(app.getApplicationId())) {
                    list.set(i, app);
                    return app;
                }
            }
            list.add(app);
        }
        return app;
    }

    public JobAlertPreference getAlertPreferences(Long profileId) {
        return alertPreferences.computeIfAbsent(profileId, k -> {
            JobAlertPreference pref = new JobAlertPreference();
            pref.setProfileId(profileId);
            pref.setEnabled(true);
            pref.setReceiveEmailAlerts(true);
            pref.setFrequency("DAILY");
            return pref;
        });
    }

    public void saveAlertPreferences(Long profileId, JobAlertPreference pref) {
        pref.setProfileId(profileId);
        alertPreferences.put(profileId, pref);
    }

    public List<RecommendedJob> getRecommendedJobs() {
        return Collections.unmodifiableList(recommendedJobsPool);
    }
}
