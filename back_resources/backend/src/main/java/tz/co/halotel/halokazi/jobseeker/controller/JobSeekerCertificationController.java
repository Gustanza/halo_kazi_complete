package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.Certification;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.CertificationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/certifications")
public class JobSeekerCertificationController {

    private final CertificationService certificationService;

    public JobSeekerCertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Certification>>> getCertifications(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(certificationService.getCertifications(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Certification>> addCertification(
            @RequestParam(required = false) Long profileId,
            @RequestBody Certification cert) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        Certification saved = certificationService.saveCertification(targetId, cert);
        return ResponseEntity.ok(ApiResponse.success("Certification added successfully", saved));
    }

    @PutMapping("/{certId}")
    public ResponseEntity<ApiResponse<Certification>> updateCertification(
            @PathVariable Long certId,
            @RequestParam(required = false) Long profileId,
            @RequestBody Certification cert) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        cert.setCertificationId(certId);
        Certification updated = certificationService.saveCertification(targetId, cert);
        return ResponseEntity.ok(ApiResponse.success("Certification updated successfully", updated));
    }

    @DeleteMapping("/{certId}")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(
            @PathVariable Long certId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = certificationService.deleteCertification(targetId, certId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Certification deleted successfully", null));
    }
}
