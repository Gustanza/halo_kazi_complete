package tz.co.halotel.halokazi.saved.controller;

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

@RestController
@RequestMapping("/api/v1/saved-jobs")
public class SavedJobController {

    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VacancySummaryDto>>> getSavedJobs(@CurrentUser UserPrincipal currentUser) {
        List<VacancySummaryDto> savedJobs = savedJobService.getSavedJobs(currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(savedJobs));
    }

    @PostMapping("/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveJob(
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        savedJobService.saveVacancy(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success("Vacancy saved successfully", Collections.singletonMap("saved", true)));
    }

    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeSavedJob(
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        savedJobService.unsaveVacancy(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success("Vacancy removed from saved list", Collections.singletonMap("saved", false)));
    }

    @GetMapping("/{vacancyId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSavedStatus(
            @PathVariable("vacancyId") Long vacancyId,
            @CurrentUser UserPrincipal currentUser) {

        boolean isSaved = savedJobService.isSaved(currentUser.getProfileId(), vacancyId);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("saved", isSaved)));
    }
}
