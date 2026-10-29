package tz.co.halotel.halokazi.jobseeker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory representation of a jobseeker's CV.
 *
 * Contains both DB-mapped fields (matching the Oracle CV table) and
 * additional in-memory-only fields (status, isDefault, lastStep, snapshot data)
 * that will be persisted to Oracle in a future migration.
 */
public class Cv {

    // --- Fields matching Oracle CV table ---
    private Long cvId;
    private Long profileId;
    private Long templateId;
    private String cvName;
    private String headline;
    private String summaryOverride;
    private String cvLanguage = "EN";
    private Long latestPdfFileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // --- In-memory-only fields (not yet in Oracle) ---
    private CvStatus status = CvStatus.DRAFT;
    private boolean isDefault;
    private CvLastStep lastStep;
    private int version; // Optimistic locking counter

    // --- Denormalized template info ---
    private String templateCode;
    private String templateName;

    // --- Section configuration ---
    private List<CvSection> sections = new ArrayList<>();

    // --- Snapshot data (deep-copied from profile at CV creation time) ---
    private CvPersonalInfo personalInfo;
    private List<CvExperience> experiences = new ArrayList<>();
    private List<CvEducation> educations = new ArrayList<>();
    private List<CvSkill> skills = new ArrayList<>();
    private List<CvCertification> certifications = new ArrayList<>();
    private List<CvLanguageEntry> languages = new ArrayList<>();
    private List<CvProject> projects = new ArrayList<>();
    private List<CvReference> references = new ArrayList<>();

    public Cv() {}

    /** Returns a fully detached copy so callers cannot mutate stored state by reference. */
    public Cv copy() {
        Cv copy = new Cv();
        copy.cvId = cvId;
        copy.profileId = profileId;
        copy.templateId = templateId;
        copy.cvName = cvName;
        copy.headline = headline;
        copy.summaryOverride = summaryOverride;
        copy.cvLanguage = cvLanguage;
        copy.latestPdfFileId = latestPdfFileId;
        copy.createdAt = createdAt;
        copy.updatedAt = updatedAt;
        copy.deletedAt = deletedAt;
        copy.status = status;
        copy.isDefault = isDefault;
        copy.lastStep = lastStep;
        copy.version = version;
        copy.templateCode = templateCode;
        copy.templateName = templateName;
        copy.sections = sections == null ? new ArrayList<>() : sections.stream().map(s -> {
            CvSection item = new CvSection();
            item.setCvId(s.getCvId());
            item.setSectionCode(s.getSectionCode());
            item.setVisible(s.isVisible());
            item.setDisplayOrder(s.getDisplayOrder());
            item.setCustomTitle(s.getCustomTitle());
            return item;
        }).collect(Collectors.toCollection(ArrayList::new));
        copy.personalInfo = copyPersonalInfo(personalInfo);
        copy.experiences = experiences == null ? new ArrayList<>() : experiences.stream().map(Cv::copyExperience).collect(Collectors.toCollection(ArrayList::new));
        copy.educations = educations == null ? new ArrayList<>() : educations.stream().map(Cv::copyEducation).collect(Collectors.toCollection(ArrayList::new));
        copy.skills = skills == null ? new ArrayList<>() : skills.stream().map(Cv::copySkill).collect(Collectors.toCollection(ArrayList::new));
        copy.certifications = certifications == null ? new ArrayList<>() : certifications.stream().map(Cv::copyCertification).collect(Collectors.toCollection(ArrayList::new));
        copy.languages = languages == null ? new ArrayList<>() : languages.stream().map(Cv::copyLanguage).collect(Collectors.toCollection(ArrayList::new));
        copy.projects = projects == null ? new ArrayList<>() : projects.stream().map(Cv::copyProject).collect(Collectors.toCollection(ArrayList::new));
        copy.references = references == null ? new ArrayList<>() : references.stream().map(Cv::copyReference).collect(Collectors.toCollection(ArrayList::new));
        return copy;
    }

    private static CvPersonalInfo copyPersonalInfo(CvPersonalInfo source) {
        if (source == null) return null;
        CvPersonalInfo copy = new CvPersonalInfo();
        copy.setFirstName(source.getFirstName());
        copy.setLastName(source.getLastName());
        copy.setContactEmail(source.getContactEmail());
        copy.setPhone(source.getPhone());
        copy.setAlternatePhone(source.getAlternatePhone());
        copy.setHeadline(source.getHeadline());
        copy.setProfessionalSummary(source.getProfessionalSummary());
        copy.setNationality(source.getNationality());
        copy.setCurrentLocation(source.getCurrentLocation());
        copy.setRegionName(source.getRegionName());
        copy.setDistrictName(source.getDistrictName());
        copy.setAddressLine(source.getAddressLine());
        copy.setProfilePictureUrl(source.getProfilePictureUrl());
        copy.setDateOfBirth(source.getDateOfBirth());
        copy.setGender(source.getGender());
        return copy;
    }

    private static CvExperience copyExperience(CvExperience source) {
        CvExperience copy = new CvExperience();
        copy.setId(source.getId()); copy.setCompanyName(source.getCompanyName()); copy.setJobTitle(source.getJobTitle());
        copy.setEmploymentTypeName(source.getEmploymentTypeName()); copy.setLocation(source.getLocation());
        copy.setDescription(source.getDescription()); copy.setStartDate(source.getStartDate()); copy.setEndDate(source.getEndDate());
        copy.setCurrent(source.isCurrent()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    private static CvEducation copyEducation(CvEducation source) {
        CvEducation copy = new CvEducation();
        copy.setId(source.getId()); copy.setEducationLevelName(source.getEducationLevelName()); copy.setInstitutionName(source.getInstitutionName());
        copy.setQualification(source.getQualification()); copy.setFieldOfStudy(source.getFieldOfStudy()); copy.setGrade(source.getGrade());
        copy.setStartDate(source.getStartDate()); copy.setEndDate(source.getEndDate()); copy.setCurrent(source.isCurrent()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    private static CvSkill copySkill(CvSkill source) {
        CvSkill copy = new CvSkill();
        copy.setId(source.getId()); copy.setSkillName(source.getSkillName()); copy.setProficiency(source.getProficiency());
        copy.setYearsExperience(source.getYearsExperience()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    private static CvCertification copyCertification(CvCertification source) {
        CvCertification copy = new CvCertification();
        copy.setId(source.getId()); copy.setCertificationName(source.getCertificationName()); copy.setIssuingOrganization(source.getIssuingOrganization());
        copy.setCredentialId(source.getCredentialId()); copy.setIssueDate(source.getIssueDate()); copy.setExpiryDate(source.getExpiryDate());
        copy.setCertificateUrl(source.getCertificateUrl()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    private static CvLanguageEntry copyLanguage(CvLanguageEntry source) {
        CvLanguageEntry copy = new CvLanguageEntry();
        copy.setId(source.getId()); copy.setLanguageName(source.getLanguageName()); copy.setLanguageCode(source.getLanguageCode());
        copy.setProficiency(source.getProficiency()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    private static CvProject copyProject(CvProject source) {
        CvProject copy = new CvProject();
        copy.setId(source.getId()); copy.setTitle(source.getTitle()); copy.setRole(source.getRole()); copy.setDescription(source.getDescription());
        copy.setProjectUrl(source.getProjectUrl()); copy.setStartDate(source.getStartDate()); copy.setEndDate(source.getEndDate()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    private static CvReference copyReference(CvReference source) {
        CvReference copy = new CvReference();
        copy.setId(source.getId()); copy.setFullName(source.getFullName()); copy.setJobTitle(source.getJobTitle()); copy.setOrganization(source.getOrganization());
        copy.setRelationship(source.getRelationship()); copy.setEmail(source.getEmail()); copy.setPhone(source.getPhone()); copy.setSortOrder(source.getSortOrder());
        return copy;
    }

    // --- Getters & Setters ---

    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getCvName() {
        return cvName;
    }

    public void setCvName(String cvName) {
        this.cvName = cvName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummaryOverride() {
        return summaryOverride;
    }

    public void setSummaryOverride(String summaryOverride) {
        this.summaryOverride = summaryOverride;
    }

    public String getCvLanguage() {
        return cvLanguage;
    }

    public void setCvLanguage(String cvLanguage) {
        this.cvLanguage = cvLanguage;
    }

    public Long getLatestPdfFileId() {
        return latestPdfFileId;
    }

    public void setLatestPdfFileId(Long latestPdfFileId) {
        this.latestPdfFileId = latestPdfFileId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public CvStatus getStatus() {
        return status;
    }

    public void setStatus(CvStatus status) {
        this.status = status;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public CvLastStep getLastStep() {
        return lastStep;
    }

    public void setLastStep(CvLastStep lastStep) {
        this.lastStep = lastStep;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<CvSection> getSections() {
        return sections;
    }

    public void setSections(List<CvSection> sections) {
        this.sections = sections;
    }

    public CvPersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(CvPersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public List<CvExperience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<CvExperience> experiences) {
        this.experiences = experiences;
    }

    public List<CvEducation> getEducations() {
        return educations;
    }

    public void setEducations(List<CvEducation> educations) {
        this.educations = educations;
    }

    public List<CvSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<CvSkill> skills) {
        this.skills = skills;
    }

    public List<CvCertification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CvCertification> certifications) {
        this.certifications = certifications;
    }

    public List<CvLanguageEntry> getLanguages() {
        return languages;
    }

    public void setLanguages(List<CvLanguageEntry> languages) {
        this.languages = languages;
    }

    public List<CvProject> getProjects() {
        return projects;
    }

    public void setProjects(List<CvProject> projects) {
        this.projects = projects;
    }

    public List<CvReference> getReferences() {
        return references;
    }

    public void setReferences(List<CvReference> references) {
        this.references = references;
    }
}
