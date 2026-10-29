package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.WorkExperience;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.WorkExperienceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/work-experience")
public class JobSeekerWorkExperienceController {

    private final WorkExperienceService workExperienceService;

    public JobSeekerWorkExperienceController(WorkExperienceService workExperienceService) {
        this.workExperienceService = workExperienceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkExperience>>> getWorkExperiences(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(workExperienceService.getWorkExperiences(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkExperience>> addWorkExperience(
            @RequestParam(required = false) Long profileId,
            @RequestBody WorkExperience experience) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        WorkExperience saved = workExperienceService.saveWorkExperience(targetId, experience);
        return ResponseEntity.ok(ApiResponse.success("Work experience added successfully", saved));
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<WorkExperience>> updateWorkExperience(
            @PathVariable Long experienceId,
            @RequestParam(required = false) Long profileId,
            @RequestBody WorkExperience experience) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        experience.setWorkExperienceId(experienceId);
        WorkExperience updated = workExperienceService.saveWorkExperience(targetId, experience);
        return ResponseEntity.ok(ApiResponse.success("Work experience updated successfully", updated));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkExperience(
            @PathVariable Long experienceId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = workExperienceService.deleteWorkExperience(targetId, experienceId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Work experience deleted successfully", null));
    }
}
