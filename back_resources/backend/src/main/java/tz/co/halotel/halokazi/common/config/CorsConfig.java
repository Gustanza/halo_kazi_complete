package tz.co.halotel.halokazi.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    public static final String DEFAULT_ALLOWED_ORIGINS = "http://localhost:5173";
    private static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS = List.of("Accept", "Authorization", "Content-Type", "If-Match", "Origin", "X-Requested-With");
    private static final List<String> EXPOSED_HEADERS = List.of("Content-Disposition", "ETag");

    private final String allowedOrigins;

    public CorsConfig(@Value("${halokazi.cors.allowed-origins:http://localhost:5173}") String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public CorsConfiguration corsConfiguration() {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim).filter(origin -> !origin.isEmpty()).toList();
        if (origins.isEmpty()) origins = List.of(DEFAULT_ALLOWED_ORIGINS);

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setExposedHeaders(EXPOSED_HEADERS);
        configuration.setAllowCredentials(true);
        return configuration;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsConfiguration configuration = corsConfiguration();
        CorsRegistration registration = registry.addMapping("/api/**")
                .allowedOrigins(configuration.getAllowedOrigins().toArray(String[]::new))
                .allowedMethods(configuration.getAllowedMethods().toArray(String[]::new))
                .allowedHeaders(configuration.getAllowedHeaders().toArray(String[]::new))
                .exposedHeaders(configuration.getExposedHeaders().toArray(String[]::new));
        registration.allowCredentials(Boolean.TRUE.equals(configuration.getAllowCredentials()));
    }
}
