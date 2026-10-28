package tz.co.halotel.halokazi.sms;

import java.time.Clock;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;

/** Sends an SMS through the configured {@link SmsSender} and records the outcome in SMS_LOG. */
@Service
@BusinessTransaction
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final SmsSender sender;
    private final SmsLogRepository repository;
    private final Clock clock;

    public SmsService(SmsSender sender, SmsLogRepository repository, Clock clock) {
        this.sender = sender;
        this.repository = repository;
        this.clock = clock;
    }

    /**
     * @param purpose free-text category stored in SMS_LOG.PURPOSE (e.g. PHONE_VERIFICATION)
     * @throws ApiException SMS_DELIVERY_FAILED if the gateway rejects the message
     */
    public void send(Long userId, String phone, String purpose, String text) {
        try {
            String messageId = sender.send(phone, text);
            repository.save(SmsLog.sent(userId, phone, purpose, messageId, OffsetDateTime.now(clock)));
        } catch (RuntimeException e) {
            log.warn("SMS to {} failed: {}", phone, e.getMessage());
            repository.save(SmsLog.failed(userId, phone, purpose, e.getMessage()));
            throw new ApiException(ErrorCode.SMS_DELIVERY_FAILED);
        }
    }
}
