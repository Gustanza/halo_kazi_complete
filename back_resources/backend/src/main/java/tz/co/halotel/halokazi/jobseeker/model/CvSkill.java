package tz.co.halotel.halokazi.jobseeker.model;

/**
 * Snapshot of a skill entry at CV creation time.
 */
public class CvSkill {

    private Long id;
    private String skillName;
    private String proficiency;
    private Double yearsExperience;
    private int sortOrder;

    public CvSkill() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public Double getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Double yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
