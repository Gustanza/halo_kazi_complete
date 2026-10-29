package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.WorkExperience;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.WorkExperienceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/work-experience")
public class JobSeekerWorkExperienceController {

    private final WorkExperienceService workExperienceService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerWorkExperienceController(WorkExperienceService workExperienceService, CurrentJobSeekerProvider currentJobSeeker) {
        this.workExperienceService = workExperienceService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkExperience>>> getWorkExperiences() {
        return ResponseEntity.ok(ApiResponse.success(workExperienceService.getWorkExperiences(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkExperience>> addWorkExperience(@RequestBody WorkExperience experience) {
        WorkExperience saved = workExperienceService.saveWorkExperience(currentJobSeeker.getCurrentProfileId(), experience);
        return ResponseEntity.ok(ApiResponse.success("Work experience added successfully", saved));
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<WorkExperience>> updateWorkExperience(
            @PathVariable Long experienceId,
            @RequestBody WorkExperience experience) {
        experience.setWorkExperienceId(experienceId);
        WorkExperience updated = workExperienceService.saveWorkExperience(currentJobSeeker.getCurrentProfileId(), experience);
        return ResponseEntity.ok(ApiResponse.success("Work experience updated successfully", updated));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkExperience(@PathVariable Long experienceId) {
        boolean deleted = workExperienceService.deleteWorkExperience(currentJobSeeker.getCurrentProfileId(), experienceId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Work experience deleted successfully", null));
    }
}
