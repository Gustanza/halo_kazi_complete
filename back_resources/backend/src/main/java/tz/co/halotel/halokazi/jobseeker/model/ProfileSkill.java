package tz.co.halotel.halokazi.jobseeker.model;

import java.time.LocalDateTime;

public class ProfileSkill {
    private Long id;
    private Long profileId;
    private Long skillId;
    private String skillName;
    private String proficiency; // BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
    private Double yearsExperience;
    private LocalDateTime createdAt;

    public ProfileSkill() {}

    public ProfileSkill(Long id, Long profileId, Long skillId, String skillName, String proficiency, Double yearsExperience) {
        this.id = id;
        this.profileId = profileId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.proficiency = proficiency;
        this.yearsExperience = yearsExperience;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
