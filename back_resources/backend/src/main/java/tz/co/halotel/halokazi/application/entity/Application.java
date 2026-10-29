package tz.co.halotel.halokazi.application.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;

import java.time.OffsetDateTime;

@Entity
@Table(name = "application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "application_ref", length = 20, unique = true)
    private String applicationRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "cv_id", nullable = false)
    private Long cvId;

    @Lob
    @Column(name = "cover_letter", nullable = false)
    private String coverLetter;

    @Column(name = "requirements_confirmed", nullable = false)
    private Integer requirementsConfirmed;

    @Column(name = "requirements_confirmed_at", nullable = false)
    private OffsetDateTime requirementsConfirmedAt;

    @Lob
    @Column(name = "profile_snapshot", nullable = false)
    private String profileSnapshot;

    @Lob
    @Column(name = "cv_snapshot", nullable = false)
    private String cvSnapshot;

    @Column(name = "cv_snapshot_file_id", nullable = false)
    private Long cvSnapshotFileId;

    @JdbcTypeCode(SqlTypes.NUMERIC) // NUMBER(p,s) column, read as Double
    @Column(name = "profile_completion_at_submit")
    private Double profileCompletionAtSubmit;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "status_changed_by")
    private Long statusChangedBy;

    @Column(name = "status_changed_at")
    private OffsetDateTime statusChangedAt;

    @Column(name = "status_remarks", length = 1000)
    private String statusRemarks;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "last_edited_at")
    private OffsetDateTime lastEditedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Application() {
    }

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

    public Vacancy getVacancy() {
        return vacancy;
    }

    public void setVacancy(Vacancy vacancy) {
        this.vacancy = vacancy;
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

    public Integer getRequirementsConfirmed() {
        return requirementsConfirmed;
    }

    public void setRequirementsConfirmed(Integer requirementsConfirmed) {
        this.requirementsConfirmed = requirementsConfirmed;
    }

    public OffsetDateTime getRequirementsConfirmedAt() {
        return requirementsConfirmedAt;
    }

    public void setRequirementsConfirmedAt(OffsetDateTime requirementsConfirmedAt) {
        this.requirementsConfirmedAt = requirementsConfirmedAt;
    }

    public String getProfileSnapshot() {
        return profileSnapshot;
    }

    public void setProfileSnapshot(String profileSnapshot) {
        this.profileSnapshot = profileSnapshot;
    }

    public String getCvSnapshot() {
        return cvSnapshot;
    }

    public void setCvSnapshot(String cvSnapshot) {
        this.cvSnapshot = cvSnapshot;
    }

    public Long getCvSnapshotFileId() {
        return cvSnapshotFileId;
    }

    public void setCvSnapshotFileId(Long cvSnapshotFileId) {
        this.cvSnapshotFileId = cvSnapshotFileId;
    }

    public Double getProfileCompletionAtSubmit() {
        return profileCompletionAtSubmit;
    }

    public void setProfileCompletionAtSubmit(Double profileCompletionAtSubmit) {
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
