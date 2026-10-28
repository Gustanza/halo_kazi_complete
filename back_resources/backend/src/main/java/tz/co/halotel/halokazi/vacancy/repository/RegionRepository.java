package tz.co.halotel.halokazi.vacancy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.vacancy.entity.Region;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByIsActiveOrderByNameEnAsc(Integer isActive);
}
