package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Service;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.dto.*;
import tz.co.halotel.halokazi.jobseeker.model.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;

import java.time.LocalDateTime;

@Service
public class JobSeekerProfileService {

    private final JobSeekerDataStore dataStore;
    private final ProfileStrengthService strengthService;

    public JobSeekerProfileService(JobSeekerDataStore dataStore, ProfileStrengthService strengthService) {
        this.dataStore = dataStore;
        this.strengthService = strengthService;
    }

    public JobSeekerProfile getProfile(Long profileId) {
        JobSeekerProfile profile = dataStore.getProfile(profileId);
        if (profile == null) {
            throw new ResourceNotFoundException("Job seeker profile not found for ID: " + profileId);
        }
        return profile;
    }

    public JobSeekerProfileDetailDto getProfileDetail(Long profileId) {
        JobSeekerProfile profile = getProfile(profileId);
        ProfileStrengthDto strength = strengthService.calculateStrength(profileId);

        JobSeekerProfileDetailDto detail = new JobSeekerProfileDetailDto();
        detail.setProfile(profile);
        detail.setProfileStrength(strength);
        detail.setEducationList(dataStore.getEducations(profileId));
        detail.setWorkExperienceList(dataStore.getWorkExperiences(profileId));
        detail.setSkillList(dataStore.getSkills(profileId));
        detail.setCertificationList(dataStore.getCertifications(profileId));
        detail.setLanguageList(dataStore.getLanguages(profileId));
        detail.setProjectList(dataStore.getProjects(profileId));
        detail.setReferenceList(dataStore.getReferences(profileId));
        detail.setAlertPreferences(dataStore.getAlertPreferences(profileId));
        detail.setStats(getDashboardStats(profileId));
        detail.setRecommendedJobs(dataStore.getRecommendedJobs());

        return detail;
    }

    public JobSeekerProfile updatePersonalDetails(Long profileId, PersonalDetailsUpdateRequest request) {
        JobSeekerProfile profile = getProfile(profileId);

        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getNationality() != null) profile.setNationality(request.getNationality());
        if (request.getCurrentLocation() != null) profile.setCurrentLocation(request.getCurrentLocation());
        if (request.getRegionId() != null) profile.setRegionId(request.getRegionId());
        if (request.getRegionName() != null) profile.setRegionName(request.getRegionName());
        if (request.getDistrictId() != null) profile.setDistrictId(request.getDistrictId());
        if (request.getDistrictName() != null) profile.setDistrictName(request.getDistrictName());
        if (request.getAddressLine() != null) profile.setAddressLine(request.getAddressLine());
        if (request.getContactEmail() != null) profile.setContactEmail(request.getContactEmail());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getAlternatePhone() != null) profile.setAlternatePhone(request.getAlternatePhone());

        profile.setUpdatedAt(LocalDateTime.now());
        profile.setLastUpdated(LocalDateTime.now().getDayOfMonth() + " " +
                LocalDateTime.now().getMonth().name().substring(0, 3) + " " +
                LocalDateTime.now().getYear());

        // Refresh cached completion
        ProfileStrengthDto strength = strengthService.calculateStrength(profileId);
        profile.setCompletionPercentage(strength.getOverallPercentage());
        profile.setCompletionUpdatedAt(LocalDateTime.now());

        dataStore.saveProfile(profile);
        return profile;
    }

    public JobSeekerProfile updateHeadlineSummary(Long profileId, HeadlineSummaryUpdateRequest request) {
        JobSeekerProfile profile = getProfile(profileId);

        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getProfessionalSummary() != null) profile.setProfessionalSummary(request.getProfessionalSummary());

        profile.setUpdatedAt(LocalDateTime.now());
        dataStore.saveProfile(profile);
        return profile;
    }

    public JobSeekerProfile updateVisibility(Long profileId, String visibility) {
        JobSeekerProfile profile = getProfile(profileId);
        profile.setProfileVisibility(visibility);
        profile.setUpdatedAt(LocalDateTime.now());
        dataStore.saveProfile(profile);
        return profile;
    }

    public JobSeekerProfile updateProfilePicture(Long profileId, String pictureUrl) {
        JobSeekerProfile profile = getProfile(profileId);
        profile.setProfilePictureUrl(pictureUrl);
        profile.setUpdatedAt(LocalDateTime.now());
        dataStore.saveProfile(profile);
        return profile;
    }

    public JobSeekerDashboardStatsDto getDashboardStats(Long profileId) {
        int apps = dataStore.getApplications(profileId).size();
        int saved = dataStore.getSavedJobs(profileId).size();
        int alerts = dataStore.getAlertPreferences(profileId).isEnabled() ? 2 : 0;
        int cvs = 3; // 3 CVs created
        return new JobSeekerDashboardStatsDto(apps, saved, alerts, cvs);
    }
}
