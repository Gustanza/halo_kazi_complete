package tz.co.halotel.halokazi.jobseeker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.dto.*;
import tz.co.halotel.halokazi.jobseeker.model.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.model.RecommendedJob;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
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
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerProfileController(JobSeekerProfileService profileService,
                                      ProfileStrengthService strengthService,
                                      JobSeekerDataStore dataStore,
                                      CurrentJobSeekerProvider currentJobSeeker) {
        this.profileService = profileService;
        this.strengthService = strengthService;
        this.dataStore = dataStore;
        this.currentJobSeeker = currentJobSeeker;
    }

    /**
     * Get the signed-in job seeker's profile. There is no "get profile by id" endpoint - nothing
     * legitimately needs to read another job seeker's profile through this API, and the one that
     * used to exist here had no ownership check at all.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<JobSeekerProfile>> getCurrentProfile() {
        JobSeekerProfile profile = profileService.getProfile(currentJobSeeker.getCurrentProfileId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * Get comprehensive profile detail (composite with strength, education, work, skills, etc.)
     */
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<JobSeekerProfileDetailDto>> getCurrentProfileDetail() {
        JobSeekerProfileDetailDto detail = profileService.getProfileDetail(currentJobSeeker.getCurrentProfileId());
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    /**
     * Update Personal Details card (First Name, Last Name, DOB, Gender, Nationality, Location)
     */
    @PutMapping("/personal-details")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updateCurrentPersonalDetails(
            @Valid @RequestBody PersonalDetailsUpdateRequest request) {
        JobSeekerProfile updated = profileService.updatePersonalDetails(currentJobSeeker.getCurrentProfileId(), request);
        return ResponseEntity.ok(ApiResponse.success("Personal details updated successfully", updated));
    }

    /**
     * Update Headline & Professional Summary
     */
    @PatchMapping("/headline-summary")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updateHeadlineSummary(
            @Valid @RequestBody HeadlineSummaryUpdateRequest request) {
        JobSeekerProfile updated = profileService.updateHeadlineSummary(currentJobSeeker.getCurrentProfileId(), request);
        return ResponseEntity.ok(ApiResponse.success("Headline and summary updated successfully", updated));
    }

    /**
     * Update Profile Visibility (Public / Private)
     */
    @PatchMapping("/visibility")
    public ResponseEntity<ApiResponse<JobSeekerProfile>> updateVisibility(
            @Valid @RequestBody ProfileVisibilityUpdateRequest request) {
        JobSeekerProfile updated = profileService.updateVisibility(currentJobSeeker.getCurrentProfileId(), request.getVisibility());
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
        JobSeekerProfile updated = profileService.updateProfilePicture(currentJobSeeker.getCurrentProfileId(), url);
        return ResponseEntity.ok(ApiResponse.success("Profile picture updated successfully", updated));
    }

    /**
     * Get Profile Strength / Completion calculation
     */
    @GetMapping("/strength")
    public ResponseEntity<ApiResponse<ProfileStrengthDto>> getProfileStrength() {
        ProfileStrengthDto strength = strengthService.calculateStrength(currentJobSeeker.getCurrentProfileId());
        return ResponseEntity.ok(ApiResponse.success(strength));
    }

    /**
     * Get Job Seeker dashboard counts (applications, saved jobs, alerts, cvs)
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<JobSeekerDashboardStatsDto>> getStats() {
        JobSeekerDashboardStatsDto stats = profileService.getDashboardStats(currentJobSeeker.getCurrentProfileId());
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
