package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Single project entry for PUT /{cvId}/projects.
 */
public class CvProjectItemRequest {

    private Long id;

    @NotBlank(message = "Project title is required")
    @Size(max = 200, message = "Project title must be at most 200 characters")
    private String title;

    @Size(max = 150, message = "Role must be at most 150 characters")
    private String role;

    @Size(max = 4000, message = "Description must be at most 4000 characters")
    private String description;

    @Size(max = 500, message = "Project URL must be at most 500 characters")
    private String projectUrl;

    private LocalDate startDate;
    private LocalDate endDate;
    private int sortOrder;

    public CvProjectItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title.trim() : null; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role != null ? role.trim() : null; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description != null ? description.trim() : null; }
    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl != null ? projectUrl.trim() : null; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
