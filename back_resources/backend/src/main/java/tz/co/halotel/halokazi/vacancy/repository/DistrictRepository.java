package tz.co.halotel.halokazi.vacancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.vacancy.entity.District;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByRegionRegionIdAndIsActiveOrderByNameEnAsc(Long regionId, Integer isActive);
}
