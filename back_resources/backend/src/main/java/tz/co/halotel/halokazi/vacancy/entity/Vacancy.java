package tz.co.halotel.halokazi.vacancy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vacancy")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacancy_id")
    private Long vacancyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Column(name = "job_title", nullable = false, length = 200)
    private String jobTitle;

    @Lob
    @Column(name = "job_description", nullable = false)
    private String jobDescription;

    @Lob
    @Column(name = "job_requirements", nullable = false)
    private String jobRequirements;

    @Lob
    @Column(name = "qualifications", nullable = false)
    private String qualifications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "min_education_level_id")
    private EducationLevel minEducationLevel;

    @Column(name = "work_experience", nullable = false, length = 500)
    private String workExperience;

    @Column(name = "min_experience_years", nullable = false)
    private Double minExperienceYears;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @Column(name = "location_details", length = 300)
    private String locationDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_type_id", nullable = false)
    private EmploymentType employmentType;

    @Column(name = "number_of_positions", nullable = false)
    private Integer numberOfPositions;

    @Column(name = "application_deadline", nullable = false)
    private OffsetDateTime applicationDeadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private JobCategory category;

    @Column(name = "salary_min")
    private BigDecimal salaryMin;

    @Column(name = "salary_max")
    private BigDecimal salaryMax;

    @Column(name = "salary_currency", nullable = false, length = 3)
    private String salaryCurrency;

    @Column(name = "salary_period", length = 10)
    private String salaryPeriod;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "closure_reason", length = 30)
    private String closureReason;

    @Column(name = "draft_expires_at")
    private OffsetDateTime draftExpiresAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public Vacancy() {
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobRequirements() {
        return jobRequirements;
    }

    public void setJobRequirements(String jobRequirements) {
        this.jobRequirements = jobRequirements;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public EducationLevel getMinEducationLevel() {
        return minEducationLevel;
    }

    public void setMinEducationLevel(EducationLevel minEducationLevel) {
        this.minEducationLevel = minEducationLevel;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public Double getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Double minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public Integer getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(Integer numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

    public OffsetDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(OffsetDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public JobCategory getCategory() {
        return category;
    }

    public void setCategory(JobCategory category) {
        this.category = category;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(BigDecimal salaryMin) {
        this.salaryMin = salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(BigDecimal salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public String getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(String salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClosureReason() {
        return closureReason;
    }

    public void setClosureReason(String closureReason) {
        this.closureReason = closureReason;
    }

    public OffsetDateTime getDraftExpiresAt() {
        return draftExpiresAt;
    }

    public void setDraftExpiresAt(OffsetDateTime draftExpiresAt) {
        this.draftExpiresAt = draftExpiresAt;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public OffsetDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(OffsetDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
