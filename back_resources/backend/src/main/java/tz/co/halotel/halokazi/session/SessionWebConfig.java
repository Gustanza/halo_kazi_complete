package tz.co.halotel.halokazi.session;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Every /api route requires a session except the ones that create one and the public site data. */
@Configuration(proxyBeanMethods = false)
class SessionWebConfig implements WebMvcConfigurer {

    static final String[] PUBLIC_ROUTES = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/otp/**",
            "/api/auth/password/**",
            "/api/public/**"
    };

    private final SessionService sessions;

    SessionWebConfig(SessionService sessions) {
        this.sessions = sessions;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionAuthInterceptor(sessions))
                .addPathPatterns("/api/**")
                .excludePathPatterns(PUBLIC_ROUTES);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver());
    }
}
