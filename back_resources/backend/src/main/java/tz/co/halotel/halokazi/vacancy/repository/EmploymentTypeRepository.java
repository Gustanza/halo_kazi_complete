package tz.co.halotel.halokazi.vacancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.vacancy.entity.EmploymentType;

import java.util.List;

@Repository
public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Long> {
    List<EmploymentType> findByIsActiveOrderBySortOrderAsc(Integer isActive);
}
