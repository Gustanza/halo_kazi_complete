package tz.co.halotel.halokazi.jobseeker.dto;

import tz.co.halotel.halokazi.jobseeker.model.*;

import java.util.ArrayList;
import java.util.List;

public class JobSeekerProfileDetailDto {
    private JobSeekerProfile profile;
    private ProfileStrengthDto profileStrength;
    private List<Education> educationList = new ArrayList<>();
    private List<WorkExperience> workExperienceList = new ArrayList<>();
    private List<ProfileSkill> skillList = new ArrayList<>();
    private List<Certification> certificationList = new ArrayList<>();
    private List<ProfileLanguage> languageList = new ArrayList<>();
    private List<ProfileProject> projectList = new ArrayList<>();
    private List<ProfileReference> referenceList = new ArrayList<>();
    private JobAlertPreference alertPreferences;
    private JobSeekerDashboardStatsDto stats;
    private List<RecommendedJob> recommendedJobs = new ArrayList<>();

    public JobSeekerProfileDetailDto() {}

    public JobSeekerProfile getProfile() {
        return profile;
    }

    public void setProfile(JobSeekerProfile profile) {
        this.profile = profile;
    }

    public ProfileStrengthDto getProfileStrength() {
        return profileStrength;
    }

    public void setProfileStrength(ProfileStrengthDto profileStrength) {
        this.profileStrength = profileStrength;
    }

    public List<Education> getEducationList() {
        return educationList;
    }

    public void setEducationList(List<Education> educationList) {
        this.educationList = educationList;
    }

    public List<WorkExperience> getWorkExperienceList() {
        return workExperienceList;
    }

    public void setWorkExperienceList(List<WorkExperience> workExperienceList) {
        this.workExperienceList = workExperienceList;
    }

    public List<ProfileSkill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<ProfileSkill> skillList) {
        this.skillList = skillList;
    }

    public List<Certification> getCertificationList() {
        return certificationList;
    }

    public void setCertificationList(List<Certification> certificationList) {
        this.certificationList = certificationList;
    }

    public List<ProfileLanguage> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<ProfileLanguage> languageList) {
        this.languageList = languageList;
    }

    public List<ProfileProject> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProfileProject> projectList) {
        this.projectList = projectList;
    }

    public List<ProfileReference> getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(List<ProfileReference> referenceList) {
        this.referenceList = referenceList;
    }

    public JobAlertPreference getAlertPreferences() {
        return alertPreferences;
    }

    public void setAlertPreferences(JobAlertPreference alertPreferences) {
        this.alertPreferences = alertPreferences;
    }

    public JobSeekerDashboardStatsDto getStats() {
        return stats;
    }

    public void setStats(JobSeekerDashboardStatsDto stats) {
        this.stats = stats;
    }

    public List<RecommendedJob> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(List<RecommendedJob> recommendedJobs) {
        this.recommendedJobs = recommendedJobs;
    }
}
