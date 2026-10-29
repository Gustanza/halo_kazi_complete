package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileLanguage;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.LanguageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/languages")
public class JobSeekerLanguageController {

    private final LanguageService languageService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerLanguageController(LanguageService languageService, CurrentJobSeekerProvider currentJobSeeker) {
        this.languageService = languageService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileLanguage>>> getLanguages() {
        return ResponseEntity.ok(ApiResponse.success(languageService.getLanguages(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileLanguage>> addLanguage(@RequestBody ProfileLanguage lang) {
        ProfileLanguage saved = languageService.saveLanguage(currentJobSeeker.getCurrentProfileId(), lang);
        return ResponseEntity.ok(ApiResponse.success("Language added successfully", saved));
    }

    @DeleteMapping("/{langId}")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(@PathVariable Long langId) {
        boolean deleted = languageService.deleteLanguage(currentJobSeeker.getCurrentProfileId(), langId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Language deleted successfully", null));
    }
}
