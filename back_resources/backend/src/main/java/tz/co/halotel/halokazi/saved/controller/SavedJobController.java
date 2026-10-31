package tz.co.halotel.halokazi.saved.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.security.CurrentUser;
import tz.co.halotel.halokazi.common.security.UserPrincipal;
import tz.co.halotel.halokazi.saved.service.SavedJobService;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "2. Saved Jobs", description = "Endpoints for bookmarking vacancies and managing saved jobs for job seekers")
@RestController
@RequestMapping("/api/v1/saved-jobs")
public class SavedJobController {

    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    @Operation(
            summary = "Get saved vacancies",
            description = "Retrieves all vacancies bookmarked/saved by the authenticated job seeker profile."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of saved vacancies returned successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<VacancySummaryDto>>> getSavedJobs(@CurrentUser UserPrincipal currentUser) {
        List<VacancySummaryDto> savedJobs = savedJobService.getSavedJobs(currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(savedJobs));
    }

    @Operation(
            summary = "Save a vacancy",
            description = "Bookmarks/saves a vacancy to the authenticated job seeker's saved jobs list. Safe against duplicate saves."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancy saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy not found")
    })
    @PostMapping("/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveJob(
            @Parameter(description = "ID of the vacancy to save", required = true, example = "1")
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        savedJobService.saveVacancy(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success("Vacancy saved successfully", Collections.singletonMap("saved", true)));
    }

    @Operation(
            summary = "Remove a saved vacancy",
            description = "Removes a vacancy from the authenticated job seeker's saved jobs list."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancy removed from saved list")
    })
    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeSavedJob(
            @Parameter(description = "ID of the vacancy to remove", required = true, example = "1")
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        savedJobService.unsaveVacancy(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success("Vacancy removed from saved list", Collections.singletonMap("saved", false)));
    }

    @Operation(
            summary = "Check if vacancy is saved",
            description = "Checks whether a specific vacancy has already been bookmarked by the authenticated job seeker."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Saved status checked successfully")
    })
    @GetMapping("/{vacancyId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSavedStatus(
            @Parameter(description = "ID of the vacancy to check", required = true, example = "1")
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        boolean isSaved = savedJobService.isSaved(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("saved", isSaved)));
    }
}
