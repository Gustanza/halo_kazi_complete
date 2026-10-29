package tz.co.halotel.halokazi.saved.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.saved.entity.SavedVacancy;
import tz.co.halotel.halokazi.saved.entity.SavedVacancyId;

import java.util.List;

@Repository
public interface SavedVacancyRepository extends JpaRepository<SavedVacancy, SavedVacancyId> {

    @Query("SELECT s FROM SavedVacancy s JOIN FETCH s.vacancy v LEFT JOIN FETCH v.employer LEFT JOIN FETCH v.region LEFT JOIN FETCH v.employmentType WHERE s.id.profileId = :profileId ORDER BY s.savedAt DESC")
    List<SavedVacancy> findByProfileIdWithVacancy(@Param("profileId") Long profileId);

    boolean existsByIdProfileIdAndIdVacancyId(Long profileId, Long vacancyId);

    void deleteByIdProfileIdAndIdVacancyId(Long profileId, Long vacancyId);
}
