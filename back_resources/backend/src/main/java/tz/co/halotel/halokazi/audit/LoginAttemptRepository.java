package tz.co.halotel.halokazi.audit;

import org.springframework.data.jpa.repository.JpaRepository;

interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
}
