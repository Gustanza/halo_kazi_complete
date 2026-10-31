package tz.co.halotel.halokazi.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.alert.dto.JobAlertPreferencesDto;
import tz.co.halotel.halokazi.alert.service.JobAlertService;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.security.CurrentUser;
import tz.co.halotel.halokazi.common.security.UserPrincipal;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "4. Job Alerts", description = "Endpoints for configuring job alerts and retrieving tailored vacancy matches")
@RestController
@RequestMapping("/api/v1/job-alerts")
public class JobAlertController {

    private final JobAlertService jobAlertService;

    public JobAlertController(JobAlertService jobAlertService) {
        this.jobAlertService = jobAlertService;
    }

    @Operation(
            summary = "Get job alert preferences",
            description = "Retrieves the alert criteria (keyword, category, location, alert frequency) configured for the authenticated job seeker."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preferences returned successfully")
    })
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<JobAlertPreferencesDto>> getAlertPreferences(@CurrentUser UserPrincipal currentUser) {
        JobAlertPreferencesDto dto = jobAlertService.getPreferences(currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "Update job alert preferences",
            description = "Saves or updates job alert subscription criteria (keyword, category, location, frequency) for the authenticated job seeker."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preferences updated successfully")
    })
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAlertPreferences(
            @RequestBody JobAlertPreferencesDto preferences,
            @CurrentUser UserPrincipal currentUser) {

        jobAlertService.updatePreferences(currentUser.getProfileId(), preferences);
        return ResponseEntity.ok(ApiResponse.success("Job alert preferences updated successfully", Collections.singletonMap("updated", true)));
    }

    @Operation(
            summary = "Get matched vacancies",
            description = "Retrieves active vacancies that match the authenticated job seeker's alert criteria."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Matching vacancies returned successfully")
    })
    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<VacancySummaryDto>>> getMatchedVacancies(@CurrentUser UserPrincipal currentUser) {
        List<VacancySummaryDto> matches = jobAlertService.getMatchedAlertVacancies(currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(matches));
    }
}
