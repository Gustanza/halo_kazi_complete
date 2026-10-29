package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Single education entry for PUT /{cvId}/education.
 */
public class CvEducationItemRequest {

    private Long id;

    @Size(max = 100, message = "Education level must be at most 100 characters")
    private String educationLevelName;

    @NotBlank(message = "Institution name is required")
    @Size(max = 200, message = "Institution name must be at most 200 characters")
    private String institutionName;

    @NotBlank(message = "Qualification is required")
    @Size(max = 200, message = "Qualification must be at most 200 characters")
    private String qualification;

    @Size(max = 150, message = "Field of study must be at most 150 characters")
    private String fieldOfStudy;

    @Size(max = 50, message = "Grade must be at most 50 characters")
    private String grade;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private int sortOrder;

    public CvEducationItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEducationLevelName() { return educationLevelName; }
    public void setEducationLevelName(String educationLevelName) { this.educationLevelName = educationLevelName != null ? educationLevelName.trim() : null; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName != null ? institutionName.trim() : null; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification != null ? qualification.trim() : null; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy != null ? fieldOfStudy.trim() : null; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade != null ? grade.trim() : null; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
