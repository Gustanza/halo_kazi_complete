package tz.co.halotel.halokazi.sms;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Development gateway: writes the SMS text (including OTP codes) to the application log instead
 * of sending it. Active when {@code halokazi.sms.gateway=logging} (the default); a real gateway
 * registers its own {@link SmsSender} under another value.
 */
@Component
@ConditionalOnProperty(name = "halokazi.sms.gateway", havingValue = "logging", matchIfMissing = true)
public class LoggingSmsSender implements SmsSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingSmsSender.class);

    @Override
    public String send(String phone, String text) {
        log.info("[DEV SMS] to {} : {}", phone, text);
        return "dev-" + UUID.randomUUID();
    }
}
