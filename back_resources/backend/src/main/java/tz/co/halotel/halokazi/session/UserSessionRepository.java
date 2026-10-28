package tz.co.halotel.halokazi.session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    @Query("select s from UserSession s join fetch s.user where s.tokenHash = :tokenHash")
    Optional<UserSession> findByTokenHashWithUser(@Param("tokenHash") String tokenHash);
}
