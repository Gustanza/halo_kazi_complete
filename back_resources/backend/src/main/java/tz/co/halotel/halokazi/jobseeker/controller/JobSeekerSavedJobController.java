package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.SavedJob;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.SavedJobService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/saved-jobs")
public class JobSeekerSavedJobController {

    private final SavedJobService savedJobService;

    public JobSeekerSavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedJob>>> getSavedJobs(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(savedJobService.getSavedJobs(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SavedJob>> saveJob(
            @RequestParam(required = false) Long profileId,
            @RequestBody SavedJob job) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        SavedJob saved = savedJobService.saveJob(targetId, job);
        return ResponseEntity.ok(ApiResponse.success("Job saved successfully", saved));
    }

    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<ApiResponse<Void>> removeSavedJob(
            @PathVariable Long vacancyId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean removed = savedJobService.removeSavedJob(targetId, vacancyId);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Saved job removed successfully", null));
    }
}
