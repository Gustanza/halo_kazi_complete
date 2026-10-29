package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileLanguage;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.LanguageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/languages")
public class JobSeekerLanguageController {

    private final LanguageService languageService;

    public JobSeekerLanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileLanguage>>> getLanguages(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(languageService.getLanguages(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileLanguage>> addLanguage(
            @RequestParam(required = false) Long profileId,
            @RequestBody ProfileLanguage lang) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        ProfileLanguage saved = languageService.saveLanguage(targetId, lang);
        return ResponseEntity.ok(ApiResponse.success("Language added successfully", saved));
    }

    @DeleteMapping("/{langId}")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(
            @PathVariable Long langId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = languageService.deleteLanguage(targetId, langId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Language deleted successfully", null));
    }
}
