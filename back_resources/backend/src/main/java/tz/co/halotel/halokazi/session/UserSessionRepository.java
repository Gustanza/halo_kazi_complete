package tz.co.halotel.halokazi.session;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    @Query("select s from UserSession s join fetch s.user where s.tokenHash = :tokenHash")
    Optional<UserSession> findByTokenHashWithUser(@Param("tokenHash") String tokenHash);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update UserSession s set s.revokedAt = :now, s.revokeReason = :reason
             where s.user.id = :userId and s.id <> :keepSessionId and s.revokedAt is null""")
    int revokeOthers(@Param("userId") Long userId, @Param("keepSessionId") Long keepSessionId,
            @Param("reason") RevokeReason reason, @Param("now") OffsetDateTime now);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update UserSession s set s.revokedAt = :now, s.revokeReason = :reason
             where s.user.id = :userId and s.revokedAt is null""")
    int revokeAll(@Param("userId") Long userId, @Param("reason") RevokeReason reason,
            @Param("now") OffsetDateTime now);
}
