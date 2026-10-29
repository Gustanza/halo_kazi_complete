package tz.co.halotel.halokazi.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.application.dto.ApplicationDetailDto;
import tz.co.halotel.halokazi.application.dto.ApplicationSubmitRequest;
import tz.co.halotel.halokazi.application.dto.ApplicationSummaryDto;
import tz.co.halotel.halokazi.application.service.ApplicationService;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.api.PageResponse;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfileRepository;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.CurrentUser;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobSeekerProfileRepository profiles;

    public ApplicationController(ApplicationService applicationService, JobSeekerProfileRepository profiles) {
        this.applicationService = applicationService;
        this.profiles = profiles;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApplicationSummaryDto>>> getMyApplications(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @CurrentUser AuthenticatedUser currentUser) {

        PageResponse<ApplicationSummaryDto> response = applicationService.getMyApplications(profileId(currentUser), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDetailDto>> getApplicationById(
            @PathVariable("id") Long id,
            @CurrentUser AuthenticatedUser currentUser) {

        ApplicationDetailDto dto = applicationService.getApplicationById(id, profileId(currentUser));
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationSummaryDto>> submitApplication(
            @Valid @RequestBody ApplicationSubmitRequest request,
            @CurrentUser AuthenticatedUser currentUser) {

        ApplicationSummaryDto dto = applicationService.submitApplication(profileId(currentUser), currentUser.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", dto));
    }

    @GetMapping("/check/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkApplicationStatus(
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser AuthenticatedUser currentUser) {

        boolean hasApplied = applicationService.checkHasApplied(profileId(currentUser), vacancyId);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("hasApplied", hasApplied)));
    }

    /** This module is job-seeker only; every endpoint above needs the caller's profile id, not their user id. */
    private Long profileId(AuthenticatedUser user) {
        return profiles.findByUserId(user.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.JOB_SEEKERS_ONLY))
                .getId();
    }
}
