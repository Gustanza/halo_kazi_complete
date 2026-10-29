package tz.co.halotel.halokazi.jobseeker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.halotel.halokazi.common.dto.ApiResponse;
import tz.co.halotel.halokazi.jobseeker.model.ProfileProject;
import tz.co.halotel.halokazi.jobseeker.service.CurrentJobSeekerProvider;
import tz.co.halotel.halokazi.jobseeker.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobseeker/projects")
public class JobSeekerProjectController {

    private final ProjectService projectService;
    private final CurrentJobSeekerProvider currentJobSeeker;

    public JobSeekerProjectController(ProjectService projectService, CurrentJobSeekerProvider currentJobSeeker) {
        this.projectService = projectService;
        this.currentJobSeeker = currentJobSeeker;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileProject>>> getProjects() {
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjects(currentJobSeeker.getCurrentProfileId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileProject>> addProject(@RequestBody ProfileProject project) {
        ProfileProject saved = projectService.saveProject(currentJobSeeker.getCurrentProfileId(), project);
        return ResponseEntity.ok(ApiResponse.success("Project added successfully", saved));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long projectId) {
        boolean deleted = projectService.deleteProject(currentJobSeeker.getCurrentProfileId(), projectId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }
}
