package tz.co.halotel.halokazi.saved.entity;

import jakarta.persistence.*;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;

import java.time.OffsetDateTime;

@Entity
@Table(name = "saved_vacancy")
public class SavedVacancy {

    @EmbeddedId
    private SavedVacancyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vacancyId")
    @JoinColumn(name = "vacancy_id", insertable = false, updatable = false)
    private Vacancy vacancy;

    @Column(name = "saved_at", nullable = false)
    private OffsetDateTime savedAt;

    public SavedVacancy() {
    }

    public SavedVacancy(Long profileId, Vacancy vacancy) {
        this.id = new SavedVacancyId(profileId, vacancy.getVacancyId());
        this.vacancy = vacancy;
        this.savedAt = OffsetDateTime.now();
    }

    public SavedVacancyId getId() {
        return id;
    }

    public void setId(SavedVacancyId id) {
        this.id = id;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }

    public void setVacancy(Vacancy vacancy) {
        this.vacancy = vacancy;
    }

    public OffsetDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(OffsetDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
