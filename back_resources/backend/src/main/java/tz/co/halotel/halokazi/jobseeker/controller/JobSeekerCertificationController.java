package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.Certification;
import tz.co.halotel.halokazi.jobseeker.service.CertificationService;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/certifications")
public class JobSeekerCertificationController {

    private final CertificationService certificationService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerCertificationController(CertificationService certificationService, CurrentJobSeekerProvider currentJobSeeker) {
        this.certificationService = certificationService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Certification>>> getCertifications() {
        return ResponseEntity.ok(ApiResponse.success(certificationService.getCertifications(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Certification>> addCertification(@RequestBody Certification cert) {
        Certification saved = certificationService.saveCertification(currentJobSeeker.getCurrentProfileId(), cert);
        return ResponseEntity.ok(ApiResponse.success("Certification added successfully", saved));
    }

    @PutMapping("/{certId}")
    public ResponseEntity<ApiResponse<Certification>> updateCertification(
            @PathVariable Long certId,
            @RequestBody Certification cert) {
        cert.setCertificationId(certId);
        Certification updated = certificationService.saveCertification(currentJobSeeker.getCurrentProfileId(), cert);
        return ResponseEntity.ok(ApiResponse.success("Certification updated successfully", updated));
    }

    @DeleteMapping("/{certId}")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(@PathVariable Long certId) {
        boolean deleted = certificationService.deleteCertification(currentJobSeeker.getCurrentProfileId(), certId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Certification deleted successfully", null));
    }
}
