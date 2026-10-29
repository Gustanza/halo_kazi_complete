package tz.co.halotel.halokazi.jobseeker.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Maps to Oracle Table: saved_vacancy
 * Composite Primary Key: profile_id, vacancy_id
 */
@Entity
@Table(name = "saved_vacancy")
@IdClass(SavedVacancyEntity.SavedVacancyId.class)
public class SavedVacancyEntity {

    @Id
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Id
    @Column(name = "vacancy_id", nullable = false)
    private Long vacancyId;

    @Column(name = "saved_at", nullable = false)
    private OffsetDateTime savedAt;

    public SavedVacancyEntity() {}

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public OffsetDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(OffsetDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public static class SavedVacancyId implements Serializable {
        private Long profileId;
        private Long vacancyId;

        public SavedVacancyId() {}

        public SavedVacancyId(Long profileId, Long vacancyId) {
            this.profileId = profileId;
            this.vacancyId = vacancyId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SavedVacancyId that = (SavedVacancyId) o;
            return Objects.equals(profileId, that.profileId) && Objects.equals(vacancyId, that.vacancyId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(profileId, vacancyId);
        }
    }
}
