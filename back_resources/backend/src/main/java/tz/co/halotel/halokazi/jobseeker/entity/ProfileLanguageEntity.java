package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Maps to Oracle Table: profile_language
 * Composite Primary Key: profile_id, language_id
 */
@Entity
@Table(name = "profile_language")
@IdClass(ProfileLanguageEntity.ProfileLanguageId.class)
public class ProfileLanguageEntity {

    @Id
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Id
    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Column(name = "proficiency", nullable = false, length = 20)
    private String proficiency; // BASIC | CONVERSATIONAL | FLUENT | NATIVE

    public ProfileLanguageEntity() {}

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public static class ProfileLanguageId implements Serializable {
        private Long profileId;
        private Long languageId;

        public ProfileLanguageId() {}

        public ProfileLanguageId(Long profileId, Long languageId) {
            this.profileId = profileId;
            this.languageId = languageId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProfileLanguageId that = (ProfileLanguageId) o;
            return Objects.equals(profileId, that.profileId) && Objects.equals(languageId, that.languageId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(profileId, languageId);
        }
    }
}
