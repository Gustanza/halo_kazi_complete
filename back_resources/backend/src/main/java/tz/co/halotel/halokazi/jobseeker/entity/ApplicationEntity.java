package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Maps to Oracle Table: application
 */
@Entity
@Table(name = "application")
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "application_ref", nullable = false, unique = true, length = 20)
    private String applicationRef;

    @Column(name = "vacancy_id", nullable = false)
    private Long vacancyId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    @Lob
    @Column(name = "cover_letter", nullable = false)
    private String coverLetter;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "requirements_confirmed", nullable = false)
    private Boolean requirementsConfirmed = true;

    @Column(name = "requirements_confirmed_at", nullable = false)
    private OffsetDateTime requirementsConfirmedAt;

    @Column(name = "profile_completion_at_submit", precision = 5, scale = 2, nullable = false)
    private BigDecimal profileCompletionAtSubmit;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // SUBMITTED -> IN_REVIEW -> REJECTED | SHORTLISTED

    @Column(name = "status_changed_by")
    private Long statusChangedBy;

    @Column(name = "status_changed_at", nullable = false)
    private OffsetDateTime statusChangedAt;

    @Column(name = "status_remarks", length = 1000)
    private String statusRemarks;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "last_edited_at")
    private OffsetDateTime lastEditedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public ApplicationEntity() {}

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationRef() {
        return applicationRef;
    }

    public void setApplicationRef(String applicationRef) {
        this.applicationRef = applicationRef;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public Boolean getRequirementsConfirmed() {
        return requirementsConfirmed;
    }

    public void setRequirementsConfirmed(Boolean requirementsConfirmed) {
        this.requirementsConfirmed = requirementsConfirmed;
    }

    public OffsetDateTime getRequirementsConfirmedAt() {
        return requirementsConfirmedAt;
    }

    public void setRequirementsConfirmedAt(OffsetDateTime requirementsConfirmedAt) {
        this.requirementsConfirmedAt = requirementsConfirmedAt;
    }

    public BigDecimal getProfileCompletionAtSubmit() {
        return profileCompletionAtSubmit;
    }

    public void setProfileCompletionAtSubmit(BigDecimal profileCompletionAtSubmit) {
        this.profileCompletionAtSubmit = profileCompletionAtSubmit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStatusChangedBy() {
        return statusChangedBy;
    }

    public void setStatusChangedBy(Long statusChangedBy) {
        this.statusChangedBy = statusChangedBy;
    }

    public OffsetDateTime getStatusChangedAt() {
        return statusChangedAt;
    }

    public void setStatusChangedAt(OffsetDateTime statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }

    public String getStatusRemarks() {
        return statusRemarks;
    }

    public void setStatusRemarks(String statusRemarks) {
        this.statusRemarks = statusRemarks;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public OffsetDateTime getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(OffsetDateTime lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
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
}
