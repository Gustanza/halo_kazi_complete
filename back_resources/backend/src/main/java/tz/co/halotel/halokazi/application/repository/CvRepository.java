package tz.co.halotel.halokazi.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.application.entity.Cv;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvRepository extends JpaRepository<Cv, Long> {

    List<Cv> findByProfileIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long profileId);

    Optional<Cv> findByCvIdAndProfileIdAndDeletedAtIsNull(Long cvId, Long profileId);
}
