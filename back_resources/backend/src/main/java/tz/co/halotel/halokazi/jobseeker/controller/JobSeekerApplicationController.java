package tz.co.halotel.halokazi.jobseeker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.dto.JobApplicationRequest;
import tz.co.halotel.halokazi.jobseeker.model.JobApplication;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.JobApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/applications")
public class JobSeekerApplicationController {

    private final JobApplicationService applicationService;

    public JobSeekerApplicationController(JobApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobApplication>>> getApplications(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplications(targetId)));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse<JobApplication>> getApplicationById(
            @PathVariable Long applicationId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplication(targetId, applicationId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobApplication>> submitApplication(
            @RequestParam(required = false) Long profileId,
            @Valid @RequestBody JobApplicationRequest request) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        JobApplication app = applicationService.submitApplication(targetId, request);
        return ResponseEntity.ok(ApiResponse.success("Job application submitted successfully", app));
    }
}
