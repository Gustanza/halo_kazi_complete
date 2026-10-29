package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.Education;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.EducationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/education")
public class JobSeekerEducationController {

    private final EducationService educationService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerEducationController(EducationService educationService, CurrentJobSeekerProvider currentJobSeeker) {
        this.educationService = educationService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Education>>> getEducations() {
        return ResponseEntity.ok(ApiResponse.success(educationService.getEducations(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Education>> addEducation(@RequestBody Education education) {
        Education saved = educationService.saveEducation(currentJobSeeker.getCurrentProfileId(), education);
        return ResponseEntity.ok(ApiResponse.success("Education record added successfully", saved));
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Education>> updateEducation(
            @PathVariable Long educationId,
            @RequestBody Education education) {
        education.setEducationId(educationId);
        Education updated = educationService.saveEducation(currentJobSeeker.getCurrentProfileId(), education);
        return ResponseEntity.ok(ApiResponse.success("Education record updated successfully", updated));
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(@PathVariable Long educationId) {
        boolean deleted = educationService.deleteEducation(currentJobSeeker.getCurrentProfileId(), educationId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Education record deleted successfully", null));
    }
}
