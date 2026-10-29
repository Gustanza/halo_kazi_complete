package tz.co.halotel.halokazi.jobseeker.model;

import java.time.LocalDateTime;

public class SavedJob {
    private Long id;
    private Long profileId;
    private Long vacancyId;
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
    private String location;
    private String employmentType;
    private String salaryRange;
    private String deadline;
    private LocalDateTime savedAt;

    public SavedJob() {}

    public SavedJob(Long id, Long profileId, Long vacancyId, String jobTitle, String companyName, String companyLogoUrl, String location, String employmentType, String salaryRange, String deadline) {
        this.id = id;
        this.profileId = profileId;
        this.vacancyId = vacancyId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.companyLogoUrl = companyLogoUrl;
        this.location = location;
        this.employmentType = employmentType;
        this.salaryRange = salaryRange;
        this.deadline = deadline;
        this.savedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
