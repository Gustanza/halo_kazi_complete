package tz.co.halotel.halokazi.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.alert.dto.JobAlertPreferencesDto;
import tz.co.halotel.halokazi.alert.service.JobAlertService;
import tz.co.halotel.halokazi.common.api.ApiResponse;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfileRepository;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.CurrentUser;
import tz.co.halotel.halokazi.vacancy.dto.VacancySummaryDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/job-alerts")
public class JobAlertController {

    private final JobAlertService jobAlertService;
    private final JobSeekerProfileRepository profiles;

    public JobAlertController(JobAlertService jobAlertService, JobSeekerProfileRepository profiles) {
        this.jobAlertService = jobAlertService;
        this.profiles = profiles;
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<JobAlertPreferencesDto>> getAlertPreferences(@CurrentUser AuthenticatedUser currentUser) {
        JobAlertPreferencesDto dto = jobAlertService.getPreferences(profileId(currentUser));
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAlertPreferences(
            @RequestBody JobAlertPreferencesDto preferences,
            @CurrentUser AuthenticatedUser currentUser) {

        jobAlertService.updatePreferences(profileId(currentUser), preferences);
        return ResponseEntity.ok(ApiResponse.success("Job alert preferences updated successfully", Collections.singletonMap("updated", true)));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<List<VacancySummaryDto>>> getMatchedVacancies(@CurrentUser AuthenticatedUser currentUser) {
        List<VacancySummaryDto> matches = jobAlertService.getMatchedAlertVacancies(profileId(currentUser));
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    /** This module is job-seeker only; every endpoint above needs the caller's profile id, not their user id. */
    private Long profileId(AuthenticatedUser user) {
        return profiles.findByUserId(user.userId())
                .orElseThrow(() -> new ApiException(ErrorCode.JOB_SEEKERS_ONLY))
                .getId();
    }
}
