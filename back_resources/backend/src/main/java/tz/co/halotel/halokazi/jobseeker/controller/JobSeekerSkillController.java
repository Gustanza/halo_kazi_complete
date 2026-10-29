package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileSkill;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/skills")
public class JobSeekerSkillController {

    private final SkillService skillService;

    public JobSeekerSkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileSkill>>> getSkills(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(skillService.getSkills(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileSkill>> addSkill(
            @RequestParam(required = false) Long profileId,
            @RequestBody ProfileSkill skill) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        ProfileSkill saved = skillService.saveSkill(targetId, skill);
        return ResponseEntity.ok(ApiResponse.success("Skill added successfully", saved));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @PathVariable Long skillId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = skillService.deleteSkill(targetId, skillId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully", null));
    }
}
