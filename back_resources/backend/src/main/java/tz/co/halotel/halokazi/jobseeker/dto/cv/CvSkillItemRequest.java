package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Single skill entry for PUT /{cvId}/skills.
 */
public class CvSkillItemRequest {

    private Long id;

    @NotBlank(message = "Skill name is required")
    @Size(max = 150, message = "Skill name must be at most 150 characters")
    private String skillName;

    @Size(max = 20, message = "Proficiency must be at most 20 characters")
    private String proficiency;

    private Double yearsExperience;
    private int sortOrder;

    public CvSkillItemRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName != null ? skillName.trim() : null; }

    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency != null ? proficiency.trim().toUpperCase() : null; }

    public Double getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(Double yearsExperience) { this.yearsExperience = yearsExperience; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
