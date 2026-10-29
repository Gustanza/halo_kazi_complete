package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.JobAlertPreference;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.JobAlertService;

@RestController
@RequestMapping("/api/v1/jobseeker/alerts")
public class JobSeekerAlertController {

    private final JobAlertService alertService;

    public JobSeekerAlertController(JobAlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<JobAlertPreference>> getAlertPreferences(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(alertService.getAlertPreferences(targetId)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<JobAlertPreference>> updateAlertPreferences(
            @RequestParam(required = false) Long profileId,
            @RequestBody JobAlertPreference preferences) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        JobAlertPreference updated = alertService.updateAlertPreferences(targetId, preferences);
        return ResponseEntity.ok(ApiResponse.success("Job alert preferences updated successfully", updated));
    }

    @PatchMapping("/toggle")
    public ResponseEntity<ApiResponse<JobAlertPreference>> toggleAlerts(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        JobAlertPreference toggled = alertService.toggleAlerts(targetId);
        String msg = toggled.isEnabled() ? "Job alerts turned ON" : "Job alerts turned OFF";
        return ResponseEntity.ok(ApiResponse.success(msg, toggled));
    }
}
