package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileSkill;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/skills")
public class JobSeekerSkillController {

    private final SkillService skillService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerSkillController(SkillService skillService, CurrentJobSeekerProvider currentJobSeeker) {
        this.skillService = skillService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileSkill>>> getSkills() {
        return ResponseEntity.ok(ApiResponse.success(skillService.getSkills(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileSkill>> addSkill(@RequestBody ProfileSkill skill) {
        ProfileSkill saved = skillService.saveSkill(currentJobSeeker.getCurrentProfileId(), skill);
        return ResponseEntity.ok(ApiResponse.success("Skill added successfully", saved));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable Long skillId) {
        boolean deleted = skillService.deleteSkill(currentJobSeeker.getCurrentProfileId(), skillId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully", null));
    }
}
