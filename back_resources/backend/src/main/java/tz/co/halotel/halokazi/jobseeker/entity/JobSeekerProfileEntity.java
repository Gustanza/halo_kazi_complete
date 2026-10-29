package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Maps to Oracle Table: job_seeker_profile
 * (Refer to 03_job_seeker_profile.dbml)
 */
@Entity
@Table(name = "job_seeker_profile")
public class JobSeekerProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "contact_email", length = 254)
    private String contactEmail;

    @Column(name = "alternate_phone", length = 20)
    private String alternatePhone;

    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "address_line", length = 300)
    private String addressLine;

    @Column(name = "headline", length = 150)
    private String headline;

    @Lob
    @Column(name = "professional_summary")
    private String professionalSummary;

    @Column(name = "profile_picture_file_id")
    private Long profilePictureFileId;

    @Column(name = "expected_salary_min", precision = 14, scale = 2)
    private BigDecimal expectedSalaryMin;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "receive_job_alerts", nullable = false)
    private Boolean receiveJobAlerts = true;

    @Column(name = "completion_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal completionPercentage = BigDecimal.ZERO;

    @Column(name = "completion_updated_at")
    private OffsetDateTime completionUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public JobSeekerProfileEntity() {}

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public Long getProfilePictureFileId() {
        return profilePictureFileId;
    }

    public void setProfilePictureFileId(Long profilePictureFileId) {
        this.profilePictureFileId = profilePictureFileId;
    }

    public BigDecimal getExpectedSalaryMin() {
        return expectedSalaryMin;
    }

    public void setExpectedSalaryMin(BigDecimal expectedSalaryMin) {
        this.expectedSalaryMin = expectedSalaryMin;
    }

    public Boolean getReceiveJobAlerts() {
        return receiveJobAlerts;
    }

    public void setReceiveJobAlerts(Boolean receiveJobAlerts) {
        this.receiveJobAlerts = receiveJobAlerts;
    }

    public BigDecimal getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(BigDecimal completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public OffsetDateTime getCompletionUpdatedAt() {
        return completionUpdatedAt;
    }

    public void setCompletionUpdatedAt(OffsetDateTime completionUpdatedAt) {
        this.completionUpdatedAt = completionUpdatedAt;
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
