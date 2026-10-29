package tz.co.halotel.halokazi.jobseeker.repository;

import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.common.exception.BusinessRuleException;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory data store for CV Builder and My CVs features.
 * Follows the same pattern as {@link JobSeekerDataStore}.
 *
 * Seeds 3 sample CVs for the default profile and 5 templates
 * matching the existing Oracle cv_template table.
 */
@Component
public class CvDataStore {

    private final Map<Long, Cv> cvs = new ConcurrentHashMap<>();
    private final List<CvTemplate> templates = new ArrayList<>();
    private final AtomicLong cvIdSeq = new AtomicLong(10);

    public CvDataStore() {
        seedTemplates();
        seedSampleCvs();
    }

    // ==================== Template Methods ====================

    public List<CvTemplate> getActiveTemplates() {
        return templates.stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()))
                .sorted(Comparator.comparingInt(CvTemplate::getSortOrder))
                .collect(Collectors.toList());
    }

    public CvTemplate getTemplateById(Long templateId) {
        return templates.stream()
                .filter(t -> t.getTemplateId().equals(templateId))
                .findFirst()
                .orElse(null);
    }

    public CvTemplate getTemplateByCode(String code) {
        if (code == null || code.isBlank()) return null;
        return templates.stream()
                .filter(t -> t.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    // ==================== CV CRUD Methods ====================

    public synchronized Cv getCv(Long cvId) {
        Cv cv = cvs.get(cvId);
        if (cv != null && cv.getDeletedAt() != null) {
            return null; // Soft-deleted CVs are invisible
        }
        return cv == null ? null : cv.copy();
    }

    /**
     * Returns the CV even if soft-deleted. Used for application FK checks.
     */
    public synchronized Cv getCvIncludeDeleted(Long cvId) {
        Cv cv = cvs.get(cvId);
        return cv == null ? null : cv.copy();
    }

    public synchronized List<Cv> getCvsByProfileId(Long profileId) {
        return cvs.values().stream()
                .filter(cv -> Objects.equals(cv.getProfileId(), profileId))
                .filter(cv -> cv.getDeletedAt() == null)
                .sorted(Comparator.comparing(Cv::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Cv::copy)
                .collect(Collectors.toList());
    }

    /** Returns only metadata for one page; snapshot sections are never copied for list views. */
    public synchronized CvSummaryPage getCvSummaryPage(Long profileId, long offset, int limit) {
        List<Cv> active = cvs.values().stream()
                .filter(cv -> Objects.equals(cv.getProfileId(), profileId))
                .filter(cv -> cv.getDeletedAt() == null)
                .sorted(Comparator.comparing(Cv::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        List<CvSummaryProjection> content = active.stream()
                .skip(Math.max(0, offset))
                .limit(Math.max(0, limit))
                .map(cv -> new CvSummaryProjection(cv.getCvId(), cv.getCvName(), cv.getTemplateCode(), cv.getTemplateName(),
                        cv.getStatus(), cv.isDefault(), cv.getCvLanguage(), cv.getUpdatedAt(), cv.getCreatedAt()))
                .toList();
        return new CvSummaryPage(content, active.size());
    }

    public record CvSummaryPage(List<CvSummaryProjection> content, long totalElements) {}

    public synchronized int countCvsByProfileId(Long profileId) {
        return (int) cvs.values().stream()
                .filter(cv -> Objects.equals(cv.getProfileId(), profileId))
                .filter(cv -> cv.getDeletedAt() == null)
                .count();
    }

    /** Atomically creates a CV after enforcing the per-profile limit. */
    public synchronized Cv createCv(Cv cv, int maxPerProfile) {
        int existingCount = countCvsByProfileId(cv.getProfileId());
        if (existingCount >= maxPerProfile) {
            throw BusinessRuleException.conflict("CV_LIMIT_REACHED", "Maximum number of CVs reached");
        }
        cv.setDefault(false);
        return saveCvInternal(cv, null);
    }

    /** Atomically checks source ownership/version and creates its duplicate. */
    public synchronized Cv duplicateCv(Long profileId, Long sourceCvId, int expectedVersion, Cv duplicate, int maxPerProfile) {
        Cv source = requireActiveOwnedCv(profileId, sourceCvId);
        checkVersion(source, expectedVersion);
        if (countCvsByProfileId(profileId) >= maxPerProfile) {
            throw BusinessRuleException.conflict("CV_LIMIT_REACHED", "Maximum number of CVs reached");
        }
        duplicate.setProfileId(profileId);
        duplicate.setDefault(false);
        return saveCvInternal(duplicate, null);
    }

    /**
     * Saves a detached CV copy and checks its optimistic-lock version.
     * An expected version is mandatory for updates; null is only valid for inserts.
     */
    public synchronized Cv saveCv(Cv cv, Integer expectedVersion) {
        return saveCvInternal(cv, expectedVersion);
    }

    /** Kept for in-package compatibility; new code should supply the expected version. */
    public synchronized Cv saveCv(Cv cv) {
        Integer expectedVersion = cv.getCvId() == null ? null : cv.getVersion();
        return saveCvInternal(cv, expectedVersion);
    }

    private Cv saveCvInternal(Cv cv, Integer expectedVersion) {
        LocalDateTime now = LocalDateTime.now();
        Cv existing = cv.getCvId() == null ? null : cvs.get(cv.getCvId());
        if (cv.getCvId() != null && (existing == null || existing.getDeletedAt() != null)) {
            throw new ResourceNotFoundException("CV not found");
        }
        if (existing != null && (expectedVersion == null || existing.getVersion() != expectedVersion)) {
            throw BusinessRuleException.conflict("CV_VERSION_CONFLICT", "CV was changed by another request; reload and try again");
        }
        String title = cv.getCvName() == null ? null : cv.getCvName().trim();
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("CV title is required");
        }
        if (titleExistsForProfile(cv.getProfileId(), title, cv.getCvId())) {
            throw BusinessRuleException.conflict("CV_TITLE_CONFLICT", "A CV with this title already exists");
        }

        Cv saved = cv.copy();
        saved.setCvName(title);
        if (existing == null) {
            saved.setCvId(cvIdSeq.incrementAndGet());
            saved.setCreatedAt(now);
            saved.setVersion(1);
        } else {
            saved.setCreatedAt(existing.getCreatedAt());
            saved.setVersion(existing.getVersion() + 1);
        }
        boolean anotherDefault = cvs.values().stream()
                .filter(current -> Objects.equals(current.getProfileId(), saved.getProfileId()))
                .filter(current -> current.getDeletedAt() == null)
                .filter(current -> !Objects.equals(current.getCvId(), saved.getCvId()))
                .anyMatch(current -> current.isDefault() && current.getStatus() == CvStatus.COMPLETED);
        saved.setDefault(saved.getStatus() == CvStatus.COMPLETED && !anotherDefault);
        saved.setUpdatedAt(now);
        cvs.put(saved.getCvId(), saved);
        cv.setCvId(saved.getCvId());
        cv.setCvName(saved.getCvName());
        cv.setCreatedAt(saved.getCreatedAt());
        cv.setUpdatedAt(saved.getUpdatedAt());
        cv.setVersion(saved.getVersion());
        cv.setDefault(saved.isDefault());
        return saved.copy();
    }

    /**
     * Checks if a CV title already exists for the given profile (case-insensitive).
     * Excludes the given cvId to allow renaming a CV to the same title.
     */
    public synchronized boolean titleExistsForProfile(Long profileId, String title, Long excludeCvId) {
        return cvs.values().stream()
                .filter(cv -> Objects.equals(cv.getProfileId(), profileId))
                .filter(cv -> cv.getDeletedAt() == null)
                .filter(cv -> !Objects.equals(cv.getCvId(), excludeCvId))
                .filter(cv -> cv.getCvName() != null)
                .anyMatch(cv -> cv.getCvName().equalsIgnoreCase(title));
    }

    /**
     * Clears the default flag on all CVs for the given profile.
     */
    public synchronized Cv setDefaultForProfile(Long profileId, Long cvId, int expectedVersion) {
        Cv target = requireActiveOwnedCv(profileId, cvId);
        if (target.getStatus() != CvStatus.COMPLETED) {
            throw BusinessRuleException.unprocessable("CV_DEFAULT_REQUIRES_COMPLETED", "Only a completed CV can be set as default");
        }
        checkVersion(target, expectedVersion);
        boolean alreadyOnlyDefault = target.isDefault() && cvs.values().stream()
                .filter(cv -> Objects.equals(cv.getProfileId(), profileId) && cv.getDeletedAt() == null)
                .filter(cv -> !Objects.equals(cv.getCvId(), cvId))
                .noneMatch(Cv::isDefault);
        if (alreadyOnlyDefault) return target.copy();

        LocalDateTime now = LocalDateTime.now();
        for (Cv current : new ArrayList<>(cvs.values())) {
            if (!Objects.equals(current.getProfileId(), profileId) || current.getDeletedAt() != null) continue;
            boolean shouldBeDefault = Objects.equals(current.getCvId(), cvId);
            if (current.isDefault() != shouldBeDefault) {
                Cv changed = current.copy();
                changed.setDefault(shouldBeDefault);
                if (shouldBeDefault) {
                    changed.setUpdatedAt(now);
                    changed.setVersion(current.getVersion() + 1);
                }
                cvs.put(changed.getCvId(), changed);
            }
        }
        return cvs.get(cvId).copy();
    }

    public synchronized void softDeleteCv(Long profileId, Long cvId, int expectedVersion) {
        Cv current = requireActiveOwnedCv(profileId, cvId);
        checkVersion(current, expectedVersion);
        boolean wasDefault = current.isDefault();
        Cv deleted = current.copy();
        LocalDateTime now = LocalDateTime.now();
        deleted.setDeletedAt(now);
        deleted.setDefault(false);
        deleted.setUpdatedAt(now);
        deleted.setVersion(current.getVersion() + 1);
        cvs.put(cvId, deleted);

        List<Cv> remaining = cvs.values().stream()
                .filter(cv -> Objects.equals(cv.getProfileId(), profileId) && cv.getDeletedAt() == null)
                .sorted(Comparator.comparing(Cv::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        if (wasDefault) {
            Cv promote = remaining.stream()
                    .filter(cv -> cv.getStatus() == CvStatus.COMPLETED)
                    .findFirst()
                    .orElse(null);
            if (promote != null && !promote.isDefault()) {
                Cv changed = promote.copy();
                changed.setDefault(true);
                changed.setUpdatedAt(now);
                changed.setVersion(promote.getVersion() + 1);
                cvs.put(changed.getCvId(), changed);
            }
        }
    }

    private Cv requireActiveOwnedCv(Long profileId, Long cvId) {
        Cv cv = cvs.get(cvId);
        if (cv == null || cv.getDeletedAt() != null || !Objects.equals(cv.getProfileId(), profileId)) {
            throw new ResourceNotFoundException("CV not found");
        }
        return cv;
    }

    private void checkVersion(Cv cv, int expectedVersion) {
        if (cv.getVersion() != expectedVersion) {
            throw BusinessRuleException.conflict("CV_VERSION_CONFLICT", "CV was changed by another request; reload and try again");
        }
    }

    // ==================== Seed Data ====================

    private void seedTemplates() {
        templates.add(new CvTemplate(1L, "CLASSIC", "Classic", "Kawaida",
                "Traditional CV layout with a clean, formal structure",
                "Mpangilio wa kawaida wa CV wenye muundo safi na rasmi",
                "ACTIVE", 1));
        templates.add(new CvTemplate(2L, "MODERN", "Modern", "Kisasa",
                "Contemporary design with modern typography and layout",
                "Muundo wa kisasa wenye fonti na mpangilio wa kisasa",
                "ACTIVE", 2));
        templates.add(new CvTemplate(3L, "PROFESSIONAL", "Professional", "Kitaaluma",
                "Business-focused template ideal for corporate roles",
                "Kiolezo kinachozingatia biashara, bora kwa nafasi za shirika",
                "ACTIVE", 3));
        templates.add(new CvTemplate(4L, "MINIMAL", "Minimal", "Rahisi",
                "Simple and clean design that lets your content shine",
                "Muundo rahisi na safi unaoruhusu maudhui yako kuangaza",
                "ACTIVE", 4));
        templates.add(new CvTemplate(5L, "GRADUATE", "Graduate", "Mhitimu",
                "Tailored for fresh graduates and early career professionals",
                "Imeandaliwa kwa wahitimu na wataalamu wa mapema katika kazi",
                "ACTIVE", 5));
    }

    private void seedSampleCvs() {
        Long profileId = JobSeekerDataStore.DEFAULT_PROFILE_ID;

        // --- CV 1: Default, COMPLETED, CLASSIC template ---
        Cv cv1 = new Cv();
        cv1.setCvId(1L);
        cv1.setProfileId(profileId);
        cv1.setTemplateId(1L);
        cv1.setTemplateCode("CLASSIC");
        cv1.setTemplateName("Classic");
        cv1.setCvName("John Mwangi — Marketing CV 2026");
        cv1.setHeadline("Marketing Specialist");
        cv1.setSummaryOverride("Results-driven marketing professional with 5+ years of experience in digital marketing, brand strategy and social media management.");
        cv1.setCvLanguage("EN");
        cv1.setStatus(CvStatus.COMPLETED);
        cv1.setDefault(true);
        cv1.setLastStep(CvLastStep.TEMPLATE);
        cv1.setCreatedAt(LocalDateTime.of(2026, 7, 10, 9, 0));
        cv1.setUpdatedAt(LocalDateTime.of(2026, 9, 3, 14, 30));
        cv1.setVersion(1);

        CvPersonalInfo pi1 = buildSeedPersonalInfo();
        cv1.setPersonalInfo(pi1);
        cv1.setExperiences(buildSeedExperiences());
        cv1.setEducations(buildSeedEducations());
        cv1.setSkills(buildSeedSkills());
        cv1.setCertifications(buildSeedCertifications());
        cv1.setLanguages(buildSeedLanguages());
        cv1.setProjects(buildSeedProjects());
        cv1.setReferences(buildSeedReferences());
        cv1.setSections(buildDefaultSections(1L));

        cvs.put(cv1.getCvId(), cv1);

        // --- CV 2: COMPLETED, MODERN template ---
        Cv cv2 = new Cv();
        cv2.setCvId(2L);
        cv2.setProfileId(profileId);
        cv2.setTemplateId(2L);
        cv2.setTemplateCode("MODERN");
        cv2.setTemplateName("Modern");
        cv2.setCvName("Digital Marketing Lead — Modern");
        cv2.setHeadline("Digital Marketing Lead");
        cv2.setCvLanguage("EN");
        cv2.setStatus(CvStatus.COMPLETED);
        cv2.setDefault(false);
        cv2.setLastStep(CvLastStep.TEMPLATE);
        cv2.setCreatedAt(LocalDateTime.of(2026, 8, 5, 11, 0));
        cv2.setUpdatedAt(LocalDateTime.of(2026, 8, 20, 16, 45));
        cv2.setVersion(1);

        cv2.setPersonalInfo(buildSeedPersonalInfo());
        cv2.setExperiences(buildSeedExperiences());
        cv2.setEducations(buildSeedEducations());
        cv2.setSkills(buildSeedSkills());
        cv2.setSections(buildDefaultSections(2L));

        cvs.put(cv2.getCvId(), cv2);

        // --- CV 3: DRAFT, PROFESSIONAL template ---
        Cv cv3 = new Cv();
        cv3.setCvId(3L);
        cv3.setProfileId(profileId);
        cv3.setTemplateId(3L);
        cv3.setTemplateCode("PROFESSIONAL");
        cv3.setTemplateName("Professional");
        cv3.setCvName("Brand Strategist CV (Draft)");
        cv3.setHeadline("Brand Strategy & Positioning Expert");
        cv3.setCvLanguage("EN");
        cv3.setStatus(CvStatus.DRAFT);
        cv3.setDefault(false);
        cv3.setLastStep(CvLastStep.SKILLS);
        cv3.setCreatedAt(LocalDateTime.of(2026, 9, 15, 10, 0));
        cv3.setUpdatedAt(LocalDateTime.of(2026, 9, 15, 10, 30));
        cv3.setVersion(1);

        cv3.setPersonalInfo(buildSeedPersonalInfo());
        cv3.setExperiences(buildSeedExperiences());
        cv3.setEducations(buildSeedEducations());
        cv3.setSections(buildDefaultSections(3L));

        cvs.put(cv3.getCvId(), cv3);
    }

    // ==================== Seed Helpers ====================

    private CvPersonalInfo buildSeedPersonalInfo() {
        CvPersonalInfo pi = new CvPersonalInfo();
        pi.setFirstName("John");
        pi.setLastName("Mwangi");
        pi.setContactEmail("john.mwangi@halokazi.co.tz");
        pi.setPhone("+255 712 345 678");
        pi.setAlternatePhone("+255 765 987 321");
        pi.setHeadline("Marketing Specialist");
        pi.setProfessionalSummary("Results-driven marketing professional with 5+ years of experience in digital marketing, brand strategy and social media management");
        pi.setNationality("Tanzania");
        pi.setCurrentLocation("Dar es Salaam, Tanzania");
        pi.setRegionName("Dar es Salaam");
        pi.setDistrictName("Ilala");
        pi.setAddressLine("Samora Avenue, Posta Mpya");
        pi.setProfilePictureUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80");
        pi.setDateOfBirth("1992-05-12");
        pi.setGender("Male");
        return pi;
    }

    private List<CvExperience> buildSeedExperiences() {
        List<CvExperience> list = new ArrayList<>();

        CvExperience exp1 = new CvExperience();
        exp1.setId(1L);
        exp1.setCompanyName("Halotel Tanzania PLC");
        exp1.setJobTitle("Digital Marketing Lead");
        exp1.setEmploymentTypeName("Full Time");
        exp1.setLocation("Dar es Salaam, Tanzania");
        exp1.setDescription("Leading digital campaign strategies, influencer partnerships, paid search & performance marketing for Halokazi products.");
        exp1.setStartDate(LocalDate.of(2021, 3, 1));
        exp1.setCurrent(true);
        exp1.setSortOrder(1);
        list.add(exp1);

        CvExperience exp2 = new CvExperience();
        exp2.setId(2L);
        exp2.setCompanyName("Vodacom Tanzania PLC");
        exp2.setJobTitle("Social Media & Brand Strategist");
        exp2.setEmploymentTypeName("Full Time");
        exp2.setLocation("Dar es Salaam, Tanzania");
        exp2.setDescription("Managed multi-channel brand campaigns, customer engagement funnels, and data analytics dashboards.");
        exp2.setStartDate(LocalDate.of(2018, 1, 15));
        exp2.setEndDate(LocalDate.of(2021, 2, 28));
        exp2.setCurrent(false);
        exp2.setSortOrder(2);
        list.add(exp2);

        return list;
    }

    private List<CvEducation> buildSeedEducations() {
        List<CvEducation> list = new ArrayList<>();
        CvEducation edu = new CvEducation();
        edu.setId(1L);
        edu.setEducationLevelName("Bachelor Degree");
        edu.setInstitutionName("University of Dar es Salaam (UDSM)");
        edu.setQualification("Bachelor of Commerce in Marketing");
        edu.setFieldOfStudy("Marketing & Business Administration");
        edu.setGrade("Upper Second Class (GPA 4.1)");
        edu.setStartDate(LocalDate.of(2011, 10, 1));
        edu.setEndDate(LocalDate.of(2014, 7, 30));
        edu.setCurrent(false);
        edu.setSortOrder(1);
        list.add(edu);
        return list;
    }

    private List<CvSkill> buildSeedSkills() {
        List<CvSkill> list = new ArrayList<>();
        list.add(buildSkill(1L, "Digital Marketing Strategy", "EXPERT", 6.0, 1));
        list.add(buildSkill(2L, "Social Media Management", "ADVANCED", 5.0, 2));
        list.add(buildSkill(3L, "Google Analytics & SEO", "ADVANCED", 4.5, 3));
        list.add(buildSkill(4L, "Brand Strategy & Positioning", "EXPERT", 5.5, 4));
        list.add(buildSkill(5L, "Content Creation & Copywriting", "INTERMEDIATE", 3.0, 5));
        return list;
    }

    private CvSkill buildSkill(Long id, String name, String proficiency, double years, int order) {
        CvSkill s = new CvSkill();
        s.setId(id);
        s.setSkillName(name);
        s.setProficiency(proficiency);
        s.setYearsExperience(years);
        s.setSortOrder(order);
        return s;
    }

    private List<CvCertification> buildSeedCertifications() {
        List<CvCertification> list = new ArrayList<>();
        CvCertification c1 = new CvCertification();
        c1.setId(1L);
        c1.setCertificationName("Google Ads Certified Search Specialist");
        c1.setIssuingOrganization("Google Digital Academy");
        c1.setCredentialId("GA-2023-88741");
        c1.setIssueDate(LocalDate.of(2023, 6, 1));
        c1.setExpiryDate(LocalDate.of(2026, 6, 1));
        c1.setCertificateUrl("https://skillshop.credential.net/google-ads-certified");
        c1.setSortOrder(1);
        list.add(c1);

        CvCertification c2 = new CvCertification();
        c2.setId(2L);
        c2.setCertificationName("HubSpot Inbound Marketing Professional");
        c2.setIssuingOrganization("HubSpot Academy");
        c2.setCredentialId("HS-INB-99231");
        c2.setIssueDate(LocalDate.of(2024, 2, 10));
        c2.setExpiryDate(LocalDate.of(2027, 2, 10));
        c2.setCertificateUrl("https://academy.hubspot.com/certificates/inbound");
        c2.setSortOrder(2);
        list.add(c2);
        return list;
    }

    private List<CvLanguageEntry> buildSeedLanguages() {
        List<CvLanguageEntry> list = new ArrayList<>();
        CvLanguageEntry l1 = new CvLanguageEntry();
        l1.setId(1L);
        l1.setLanguageName("English");
        l1.setLanguageCode("en");
        l1.setProficiency("FLUENT");
        l1.setSortOrder(1);
        list.add(l1);

        CvLanguageEntry l2 = new CvLanguageEntry();
        l2.setId(2L);
        l2.setLanguageName("Swahili");
        l2.setLanguageCode("sw");
        l2.setProficiency("NATIVE");
        l2.setSortOrder(2);
        list.add(l2);
        return list;
    }

    private List<CvProject> buildSeedProjects() {
        List<CvProject> list = new ArrayList<>();
        CvProject p = new CvProject();
        p.setId(1L);
        p.setTitle("National Halokazi Launch Campaign");
        p.setRole("Campaign Director");
        p.setDescription("Executed a nationwide digital rollout campaign across 15 regions of Tanzania, resulting in 500k+ downloads.");
        p.setProjectUrl("https://halokazi.co.tz/campaigns/launch");
        p.setStartDate(LocalDate.of(2025, 1, 10));
        p.setEndDate(LocalDate.of(2025, 6, 30));
        p.setSortOrder(1);
        list.add(p);
        return list;
    }

    private List<CvReference> buildSeedReferences() {
        List<CvReference> list = new ArrayList<>();
        CvReference r = new CvReference();
        r.setId(1L);
        r.setFullName("Amina Bakari");
        r.setJobTitle("Head of Marketing");
        r.setOrganization("Vodacom Tanzania PLC");
        r.setRelationship("Former Supervisor");
        r.setEmail("amina.bakari@vodacom.co.tz");
        r.setPhone("+255 754 112 233");
        r.setSortOrder(1);
        list.add(r);
        return list;
    }

    private List<CvSection> buildDefaultSections(Long cvId) {
        List<CvSection> sections = new ArrayList<>();
        int order = 1;
        for (CvSectionCode code : CvSectionCode.values()) {
            // Default: CONTACT and SUMMARY always visible;
            // ACHIEVEMENTS and INTERESTS hidden by default
            boolean visible = code != CvSectionCode.ACHIEVEMENTS && code != CvSectionCode.INTERESTS;
            sections.add(new CvSection(cvId, code.name(), visible, order++, null));
        }
        return sections;
    }
}
