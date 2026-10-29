package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileReference;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.ReferenceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/references")
public class JobSeekerReferenceController {

    private final ReferenceService referenceService;

    public JobSeekerReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileReference>>> getReferences(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(referenceService.getReferences(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileReference>> addReference(
            @RequestParam(required = false) Long profileId,
            @RequestBody ProfileReference reference) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        ProfileReference saved = referenceService.saveReference(targetId, reference);
        return ResponseEntity.ok(ApiResponse.success("Reference added successfully", saved));
    }

    @DeleteMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<Void>> deleteReference(
            @PathVariable Long referenceId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = referenceService.deleteReference(targetId, referenceId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Reference deleted successfully", null));
    }
}
