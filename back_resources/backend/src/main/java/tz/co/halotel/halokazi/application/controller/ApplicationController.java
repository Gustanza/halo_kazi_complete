package tz.co.halotel.halokazi.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "3. Job Applications", description = "Endpoints for submitting job applications, viewing submission history, and checking application status")
@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(
            summary = "Get user applications",
            description = "Retrieves paginated list of job applications submitted by the authenticated job seeker profile."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Applications retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApplicationSummaryDto>>> getMyApplications(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @CurrentUser UserPrincipal currentUser) {

        PageResponse<ApplicationSummaryDto> response = applicationService.getMyApplications(currentUser.getProfileId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "Get application details by ID",
            description = "Retrieves detailed information for a submitted application including the CV snapshot, cover letter, and status change audit timeline."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application details found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found or unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDetailDto>> getApplicationById(
            @Parameter(description = "ID of the application", required = true, example = "1")
            @PathVariable("id") Long id,
            @CurrentUser UserPrincipal currentUser) {

        ApplicationDetailDto dto = applicationService.getApplicationById(id, currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "Submit a job application",
            description = "Submits an application for an open vacancy. Automatically validates that the user hasn't already applied, the vacancy is still accepting applications, and generates the immutable CV/profile snapshot."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error, duplicate application, or vacancy closed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy or CV not found")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationSummaryDto>> submitApplication(
            @Valid @RequestBody ApplicationSubmitRequest request,
            @CurrentUser UserPrincipal currentUser) {

        ApplicationSummaryDto dto = applicationService.submitApplication(currentUser.getProfileId(), currentUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", dto));
    }

    @Operation(
            summary = "Check application status for vacancy",
            description = "Checks whether the authenticated job seeker has already submitted an application for the specified vacancy."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application status checked successfully")
    })
    @GetMapping("/check/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkApplicationStatus(
            @Parameter(description = "ID of the vacancy to check", required = true, example = "1")
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        boolean hasApplied = applicationService.checkHasApplied(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("hasApplied", hasApplied)));
    }
}
