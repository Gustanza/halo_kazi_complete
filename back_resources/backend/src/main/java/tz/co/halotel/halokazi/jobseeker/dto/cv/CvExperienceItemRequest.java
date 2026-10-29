package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Single work experience entry for PUT /{cvId}/experience.
 * The endpoint receives a {@code List<CvExperienceItemRequest>}.
 * Items with a non-null {@code id} are updates; null {@code id} means insert new.
 * Items present in the CV but absent from the request are removed (diff/merge).
 */
public class CvExperienceItemRequest {

    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must be at most 200 characters")
    private String companyName;

    @NotBlank(message = "Job title is required")
    @Size(max = 150, message = "Job title must be at most 150 characters")
    private String jobTitle;

    @Size(max = 100, message = "Employment type must be at most 100 characters")
    private String employmentTypeName;

    @Size(max = 150, message = "Location must be at most 150 characters")
    private String location;

    @Size(max = 4000, message = "Description must be at most 4000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private boolean current;

    private int sortOrder;

    public CvExperienceItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName != null ? companyName.trim() : null; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle != null ? jobTitle.trim() : null; }

    public String getEmploymentTypeName() { return employmentTypeName; }
    public void setEmploymentTypeName(String employmentTypeName) { this.employmentTypeName = employmentTypeName != null ? employmentTypeName.trim() : null; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location != null ? location.trim() : null; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description != null ? description.trim() : null; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
