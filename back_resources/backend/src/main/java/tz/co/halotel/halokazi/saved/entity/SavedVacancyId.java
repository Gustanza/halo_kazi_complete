package tz.co.halotel.halokazi.saved.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SavedVacancyId implements Serializable {

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "vacancy_id")
    private Long vacancyId;

    public SavedVacancyId() {
    }

    public SavedVacancyId(Long profileId, Long vacancyId) {
        this.profileId = profileId;
        this.vacancyId = vacancyId;
    }

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
