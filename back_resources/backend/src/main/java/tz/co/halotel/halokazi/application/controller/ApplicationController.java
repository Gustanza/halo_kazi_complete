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
import tz.co.halotel.halokazi.common.security.CurrentUser;
import tz.co.halotel.halokazi.common.security.UserPrincipal;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApplicationSummaryDto>>> getMyApplications(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @CurrentUser UserPrincipal currentUser) {

        PageResponse<ApplicationSummaryDto> response = applicationService.getMyApplications(currentUser.getProfileId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDetailDto>> getApplicationById(
            @PathVariable("id") Long id,
            @CurrentUser UserPrincipal currentUser) {

        ApplicationDetailDto dto = applicationService.getApplicationById(id, currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationSummaryDto>> submitApplication(
            @Valid @RequestBody ApplicationSubmitRequest request,
            @CurrentUser UserPrincipal currentUser) {

        ApplicationSummaryDto dto = applicationService.submitApplication(currentUser.getProfileId(), currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", dto));
    }

    @GetMapping("/check/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkApplicationStatus(
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        boolean hasApplied = applicationService.checkHasApplied(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("hasApplied", hasApplied)));
    }
}
