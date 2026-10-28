package tz.co.halotel.halokazi.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** Replaces the dev LoggingSmsSender with a capturing one (it wins as @Primary). */
@TestConfiguration(proxyBeanMethods = false)
public class TestSmsConfig {

    @Bean
    @Primary
    CapturingSmsSender capturingSmsSender() {
        return new CapturingSmsSender();
    }
}
