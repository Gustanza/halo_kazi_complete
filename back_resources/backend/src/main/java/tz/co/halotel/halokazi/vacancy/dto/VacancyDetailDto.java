package tz.co.halotel.halokazi.vacancy.dto;

import java.time.OffsetDateTime;

public class VacancyDetailDto extends VacancySummaryDto {

    private String description;
    private String requirements;
    private String qualifications;
    private String minEducationLevel;
    private Long minEducationLevelId;
    private String workExperience;
    private Double minExperienceYears;
    private String district;
    private Long districtId;
    private String locationDetails;
    private String companyDescription;
    private String companyWebsite;
    private String companyEmail;
    private String companyPhone;
    private String status;
    private boolean hasApplied;

    public VacancyDetailDto() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getMinEducationLevel() {
        return minEducationLevel;
    }

    public void setMinEducationLevel(String minEducationLevel) {
        this.minEducationLevel = minEducationLevel;
    }

    public Long getMinEducationLevelId() {
        return minEducationLevelId;
    }

    public void setMinEducationLevelId(Long minEducationLevelId) {
        this.minEducationLevelId = minEducationLevelId;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHasApplied() {
        return hasApplied;
    }

    public void setHasApplied(boolean hasApplied) {
        this.hasApplied = hasApplied;
    }
}
