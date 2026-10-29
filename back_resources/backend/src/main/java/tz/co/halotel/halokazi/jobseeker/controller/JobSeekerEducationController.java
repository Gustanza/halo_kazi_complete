package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.Education;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.EducationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/education")
public class JobSeekerEducationController {

    private final EducationService educationService;

    public JobSeekerEducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Education>>> getEducations(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(educationService.getEducations(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Education>> addEducation(
            @RequestParam(required = false) Long profileId,
            @RequestBody Education education) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        Education saved = educationService.saveEducation(targetId, education);
        return ResponseEntity.ok(ApiResponse.success("Education record added successfully", saved));
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Education>> updateEducation(
            @PathVariable Long educationId,
            @RequestParam(required = false) Long profileId,
            @RequestBody Education education) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        education.setEducationId(educationId);
        Education updated = educationService.saveEducation(targetId, education);
        return ResponseEntity.ok(ApiResponse.success("Education record updated successfully", updated));
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable Long educationId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = educationService.deleteEducation(targetId, educationId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Education record deleted successfully", null));
    }
}
