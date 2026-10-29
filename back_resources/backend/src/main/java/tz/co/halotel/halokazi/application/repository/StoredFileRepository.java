package tz.co.halotel.halokazi.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.halotel.halokazi.application.entity.StoredFile;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
}
