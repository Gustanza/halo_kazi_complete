package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Maps to Oracle Table: cv
 * FK: profile_id -> job_seeker_profile, template_id -> cv_template,
 *     latest_pdf_file_id -> stored_file
 */
@Entity
@Table(name = "cv")
public class CvEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cv_id")
    private Long cvId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "cv_name", nullable = false, length = 100)
    private String cvName;

    @Column(name = "headline", length = 150)
    private String headline;

    @Lob
    @Column(name = "summary_override")
    private String summaryOverride;

    @Column(name = "cv_language", nullable = false, length = 2)
    private String cvLanguage = "EN";

    @Column(name = "latest_pdf_file_id")
    private Long latestPdfFileId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public CvEntity() {}

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
