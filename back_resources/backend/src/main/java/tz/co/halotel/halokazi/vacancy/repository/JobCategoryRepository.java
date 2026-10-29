package tz.co.halotel.halokazi.vacancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.vacancy.entity.JobCategory;

import java.util.List;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
    List<JobCategory> findByIsActiveOrderBySortOrderAsc(Integer isActive);
}
