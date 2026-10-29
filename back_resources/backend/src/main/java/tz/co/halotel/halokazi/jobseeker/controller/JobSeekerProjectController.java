package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileProject;
import tz.co.halotel.halokazi.jobseeker.repository.JobSeekerDataStore;
import tz.co.halotel.halokazi.jobseeker.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/projects")
public class JobSeekerProjectController {

    private final ProjectService projectService;

    public JobSeekerProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileProject>>> getProjects(
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjects(targetId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileProject>> addProject(
            @RequestParam(required = false) Long profileId,
            @RequestBody ProfileProject project) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        ProfileProject saved = projectService.saveProject(targetId, project);
        return ResponseEntity.ok(ApiResponse.success("Project added successfully", saved));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long profileId) {
        Long targetId = profileId != null ? profileId : JobSeekerDataStore.DEFAULT_PROFILE_ID;
        boolean deleted = projectService.deleteProject(targetId, projectId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }
}
