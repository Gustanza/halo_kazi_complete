package tz.co.halotel.halokazi.otp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    /** The code currently in play for this user and purpose. */
    Optional<OtpCode> findFirstByUserIdAndPurposeAndConsumedAtIsNullOrderByIdDesc(Long userId, OtpPurpose purpose);
}
