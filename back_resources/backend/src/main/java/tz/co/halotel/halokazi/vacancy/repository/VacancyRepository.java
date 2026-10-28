package tz.co.halotel.halokazi.vacancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;

import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long>, JpaSpecificationExecutor<Vacancy> {

    Optional<Vacancy> findByVacancyIdAndDeletedAtIsNull(Long vacancyId);
}
