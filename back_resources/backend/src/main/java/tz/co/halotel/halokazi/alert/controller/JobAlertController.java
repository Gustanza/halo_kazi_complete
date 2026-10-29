package tz.co.halotel.halokazi.alert.controller;

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

@RestController
@RequestMapping("/api/v1/job-alerts")
public class JobAlertController {

    private final JobAlertService jobAlertService;

    public JobAlertController(JobAlertService jobAlertService) {
        this.jobAlertService = jobAlertService;
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<JobAlertPreferencesDto>> getAlertPreferences(@CurrentUser UserPrincipal currentUser) {
        JobAlertPreferencesDto dto = jobAlertService.getPreferences(currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAlertPreferences(
            @RequestBody JobAlertPreferencesDto preferences,
            @CurrentUser UserPrincipal currentUser) {

        jobAlertService.updatePreferences(currentUser.getProfileId(), preferences);
        return ResponseEntity.ok(ApiResponse.success("Job alert preferences updated successfully", Collections.singletonMap("updated", true)));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<VacancySummaryDto>>> getMatchedVacancies(@CurrentUser UserPrincipal currentUser) {
        List<VacancySummaryDto> matches = jobAlertService.getMatchedAlertVacancies(currentUser.getProfileId());
        return ResponseEntity.ok(ApiResponse.success(matches));
    }
}
