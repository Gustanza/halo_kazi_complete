package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileReference;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.ReferenceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/references")
public class JobSeekerReferenceController {

    private final ReferenceService referenceService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerReferenceController(ReferenceService referenceService, CurrentJobSeekerProvider currentJobSeeker) {
        this.referenceService = referenceService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileReference>>> getReferences() {
        return ResponseEntity.ok(ApiResponse.success(referenceService.getReferences(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileReference>> addReference(@RequestBody ProfileReference reference) {
        ProfileReference saved = referenceService.saveReference(currentJobSeeker.getCurrentProfileId(), reference);
        return ResponseEntity.ok(ApiResponse.success("Reference added successfully", saved));
    }

    @DeleteMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<Void>> deleteReference(@PathVariable Long referenceId) {
        boolean deleted = referenceService.deleteReference(currentJobSeeker.getCurrentProfileId(), referenceId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Reference deleted successfully", null));
    }
}
