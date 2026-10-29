package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Maps to Oracle Table: profile_skill
 * Composite Primary Key: profile_id, skill_id
 */
@Entity
@Table(name = "profile_skill")
@IdClass(ProfileSkillEntity.ProfileSkillId.class)
public class ProfileSkillEntity {

    @Id
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Id
    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "proficiency", nullable = false, length = 20)
    private String proficiency; // BEGINNER | INTERMEDIATE | ADVANCED | EXPERT

    @Column(name = "years_experience", precision = 4, scale = 1)
    private BigDecimal yearsExperience;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ProfileSkillEntity() {}

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

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public BigDecimal getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(BigDecimal yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class ProfileSkillId implements Serializable {
        private Long profileId;
        private Long skillId;

        public ProfileSkillId() {}

        public ProfileSkillId(Long profileId, Long skillId) {
            this.profileId = profileId;
            this.skillId = skillId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProfileSkillId that = (ProfileSkillId) o;
            return Objects.equals(profileId, that.profileId) && Objects.equals(skillId, that.skillId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(profileId, skillId);
        }
    }
}
