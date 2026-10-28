package tz.co.halotel.halokazi.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tz.co.halotel.halokazi.common.security.UserPrincipalResolver;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserPrincipalResolver userPrincipalResolver;

    public WebMvcConfig(UserPrincipalResolver userPrincipalResolver) {
        this.userPrincipalResolver = userPrincipalResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userPrincipalResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
