package tz.co.halotel.halokazi.sms;

import org.springframework.data.jpa.repository.JpaRepository;

interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
}
