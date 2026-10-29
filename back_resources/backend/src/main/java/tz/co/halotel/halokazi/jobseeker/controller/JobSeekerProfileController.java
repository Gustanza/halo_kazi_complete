package tz.co.halotel.halokazi.jobseeker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.dto.*;
import tz.co.halotel.halokazi.jobseeker.model.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.model.RecommendedJob;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.JobSeekerProfileService;
import tz.co.halotel.halokazi.jobseeker.service.ProfileStrengthService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/jobseeker/profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService profileService;
    private final ProfileStrengthService strengthService;
    private final JobSeekerDataStore dataStore;

    public JobSeekerProfileController(JobSeekerProfileService profileService,
                                      ProfileStrengthService strengthService,
                                      JobSeekerDataStore dataStore) {
        this.profileService = profileService;
        this.strengthService = strengthService;
        this.dataStore = dataStore;
    }

    /**
     * Get current authenticated Job Seeker's profile (Defaults to John Mwangi profile ID 1)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<JobSeekerProfile>> getCurrentProfile() {
        JobSeekerProfile profile = profileService.getProfile(JobSeekerDataStore.DEFAULT_PROFILE_ID);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * Get profile by ID
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> getProfileById(@PathVariable Long profileId) {
        JobSeekerProfile profile = profileService.getProfile(profileId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * Get comprehensive profile detail (composite with strength, education, work, skills, etc.)
     */
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<JobSeekerProfileDetailDto>> getCurrentProfileDetail() {
        JobSeekerProfileDetailDto detail = profileService.getProfileDetail(JobSeekerDataStore.DEFAULT_PROFILE_ID);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @GetMapping("/{profileId}/detail")
    public ResponseEntity<ApiResponse<JobSeekerProfileDetailDto>> getProfileDetailById(@PathVariable Long profileId) {
        JobSeekerProfileDetailDto detail = profileService.getProfileDetail(profileId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    /**
     * Update Personal Details card (First Name, Last Name, DOB, Gender, Nationality, Location)
     */
    @PutMapping("/personal-details")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updateCurrentPersonalDetails(
            @Valid @RequestBody PersonalDetailsUpdateRequest request) {
        JobSeekerProfile updated = profileService.updatePersonalDetails(JobSeekerDataStore.DEFAULT_PROFILE_ID, request);
        return ResponseEntity.ok(ApiResponse.success("Personal details updated successfully", updated));
    }

    @PutMapping("/{profileId}/personal-details")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updatePersonalDetails(
            @PathVariable Long profileId,
            @Valid @RequestBody PersonalDetailsUpdateRequest request) {
        JobSeekerProfile updated = profileService.updatePersonalDetails(profileId, request);
        return ResponseEntity.ok(ApiResponse.success("Personal details updated successfully", updated));
    }

    /**
     * Update Headline & Professional Summary
     */
    @PatchMapping("/headline-summary")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updateHeadlineSummary(
            @Valid @RequestBody HeadlineSummaryUpdateRequest request) {
        JobSeekerProfile updated = profileService.updateHeadlineSummary(JobSeekerDataStore.DEFAULT_PROFILE_ID, request);
        return ResponseEntity.ok(ApiResponse.success("Headline and summary updated successfully", updated));
    }

    /**
     * Update Profile Visibility (Public / Private)
     */
    @PatchMapping("/visibility")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updateVisibility(
            @Valid @RequestBody ProfileVisibilityUpdateRequest request) {
        JobSeekerProfile updated = profileService.updateVisibility(JobSeekerDataStore.DEFAULT_PROFILE_ID, request.getVisibility());
        return ResponseEntity.ok(ApiResponse.success("Profile visibility set to " + request.getVisibility(), updated));
    }

    /**
     * Update Profile Picture URL
     */
    @PatchMapping("/picture")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updatePicture(
            @RequestBody Map<String, String> payload) {
        String url = payload.get("pictureUrl");
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("pictureUrl is required"));
        }
        JobSeekerProfile updated = profileService.updateProfilePicture(JobSeekerDataStore.DEFAULT_PROFILE_ID, url);
        return ResponseEntity.ok(ApiResponse.success("Profile picture updated successfully", updated));
    }

    /**
     * Get Profile Strength / Completion calculation
     */
    @GetMapping("/strength")
    public ResponseEntity<ApiResponse<ProfileStrengthDto>> getProfileStrength() {
        ProfileStrengthDto strength = strengthService.calculateStrength(JobSeekerDataStore.DEFAULT_PROFILE_ID);
        return ResponseEntity.ok(ApiResponse.success(strength));
    }

    /**
     * Get Job Seeker dashboard counts (applications, saved jobs, alerts, cvs)
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<JobSeekerDashboardStatsDto>> getStats() {
        JobSeekerDashboardStatsDto stats = profileService.getDashboardStats(JobSeekerDataStore.DEFAULT_PROFILE_ID);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get Recommended jobs for Job Seeker
     */
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendedJob>>> getRecommendations() {
        return ResponseEntity.ok(ApiResponse.success(dataStore.getRecommendedJobs()));
    }
}
