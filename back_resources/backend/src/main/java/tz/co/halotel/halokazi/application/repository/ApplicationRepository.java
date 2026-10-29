package tz.co.halotel.halokazi.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.application.entity.Application;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT a FROM Application a JOIN FETCH a.vacancy v LEFT JOIN FETCH v.employer LEFT JOIN FETCH v.region LEFT JOIN FETCH v.employmentType WHERE a.profileId = :profileId ORDER BY a.submittedAt DESC")
    Page<Application> findByProfileIdWithVacancy(@Param("profileId") Long profileId, Pageable pageable);

    Optional<Application> findByApplicationIdAndProfileId(Long applicationId, Long profileId);

    boolean existsByProfileIdAndVacancyVacancyIdAndStatusNot(Long profileId, Long vacancyId, String status);
}
