package tz.co.halotel.halokazi.jobseeker.dto.cv;

import tz.co.halotel.halokazi.jobseeker.model.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Full CV details for the CV Builder editor and CV detail view.
 * Contains all section snapshot data and section configuration.
 *
 * <p>Design decision: section snapshot data uses the Phase-1 model classes
 * (CvPersonalInfo, CvExperience, etc.) directly, since they are pure POJOs
 * with no JPA annotations or business logic. This avoids duplicating 8
 * nearly-identical response DTO classes.</p>
 */
public class CvDetailDto {

    // --- CV metadata ---
    private Long cvId;
    private String cvName;
    private Long templateId;
    private String templateCode;
    private String templateName;
    private String status;
    private boolean isDefault;
    private String lastStep;
    private String cvLanguage;
    private String headline;
    private String summaryOverride;
    private int version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Snapshot section data ---
    private CvPersonalInfo personalInfo;
    private List<CvExperience> experiences;
    private List<CvEducation> educations;
    private List<CvSkill> skills;
    private List<CvCertification> certifications;
    private List<CvLanguageEntry> languages;
    private List<CvProject> projects;
    private List<CvReference> references;

    // --- Section configuration ---
    private List<SectionConfig> sectionConfig;

    public CvDetailDto() {}

    // --- Getters & Setters ---

    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
    public String getCvName() { return cvName; }
    public void setCvName(String cvName) { this.cvName = cvName; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public String getLastStep() { return lastStep; }
    public void setLastStep(String lastStep) { this.lastStep = lastStep; }
    public String getCvLanguage() { return cvLanguage; }
    public void setCvLanguage(String cvLanguage) { this.cvLanguage = cvLanguage; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getSummaryOverride() { return summaryOverride; }
    public void setSummaryOverride(String summaryOverride) { this.summaryOverride = summaryOverride; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public CvPersonalInfo getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(CvPersonalInfo personalInfo) { this.personalInfo = personalInfo; }
    public List<CvExperience> getExperiences() { return experiences; }
    public void setExperiences(List<CvExperience> experiences) { this.experiences = experiences; }
    public List<CvEducation> getEducations() { return educations; }
    public void setEducations(List<CvEducation> educations) { this.educations = educations; }
    public List<CvSkill> getSkills() { return skills; }
    public void setSkills(List<CvSkill> skills) { this.skills = skills; }
    public List<CvCertification> getCertifications() { return certifications; }
    public void setCertifications(List<CvCertification> certifications) { this.certifications = certifications; }
    public List<CvLanguageEntry> getLanguages() { return languages; }
    public void setLanguages(List<CvLanguageEntry> languages) { this.languages = languages; }
    public List<CvProject> getProjects() { return projects; }
    public void setProjects(List<CvProject> projects) { this.projects = projects; }
    public List<CvReference> getReferences() { return references; }
    public void setReferences(List<CvReference> references) { this.references = references; }
    public List<SectionConfig> getSectionConfig() { return sectionConfig; }
    public void setSectionConfig(List<SectionConfig> sectionConfig) { this.sectionConfig = sectionConfig; }

    /**
     * Nested static class for section visibility/ordering in the response.
     */
    public static class SectionConfig {
        private String sectionCode;
        private String title;
        private boolean visible;
        private int displayOrder;

        public SectionConfig() {}

        public SectionConfig(String sectionCode, String title, boolean visible, int displayOrder) {
            this.sectionCode = sectionCode;
            this.title = title;
            this.visible = visible;
            this.displayOrder = displayOrder;
        }

        public String getSectionCode() { return sectionCode; }
        public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public boolean isVisible() { return visible; }
        public void setVisible(boolean visible) { this.visible = visible; }
        public int getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    }
}
