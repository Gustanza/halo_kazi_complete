package tz.co.halotel.halokazi.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.co.halotel.halokazi.common.util.Emails;
import tz.co.halotel.halokazi.common.util.PhoneNumbers;

/** Phone must be E.164 and email already normalised (see PhoneNumbers / Emails). */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByPhone(String phone);

    // lower(email) matches the uq_app_user_email function-based index.
    @Query("select u from AppUser u where lower(u.email) = :email")
    Optional<AppUser> findByNormalizedEmail(@Param("email") String email);

    @Query("select count(u) > 0 from AppUser u where lower(u.email) = :email")
    boolean existsByNormalizedEmail(@Param("email") String email);

    /** AUTH-04: what people type to sign in - a phone number in any accepted format, or an email. */
    default Optional<AppUser> findByLoginIdentifier(String identifier) {
        if (Emails.looksLikeEmail(identifier)) {
            return findByNormalizedEmail(Emails.normalize(identifier));
        }
        return PhoneNumbers.normalize(identifier).flatMap(this::findByPhone);
    }
}
