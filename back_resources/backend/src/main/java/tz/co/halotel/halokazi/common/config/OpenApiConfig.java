package tz.co.halotel.halokazi.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tz.co.halotel.halokazi.common.security.UserPrincipal;

@Configuration
public class OpenApiConfig {

    static {
        // Prevent SpringDoc from exposing internal resolved user principal as query parameters in documentation
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(UserPrincipal.class);
    }

    @Bean
    public OpenAPI halokaziOpenAPI() {
        final String userIdHeader = "X-User-Id";
        final String profileIdHeader = "X-Profile-Id";

        return new OpenAPI()
                .info(new Info()
                        .title("Halokazi Backend API")
                        .description("Backend REST API for the Halokazi career, job opportunity, and CV builder platform.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Halokazi Backend Team")
                                .email("dev@halotel.co.tz")))
                .components(new Components()
                        .addSecuritySchemes(userIdHeader, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(userIdHeader)
                                .description("Logged-in User ID (e.g. 1 for demo job seeker)"))
                        .addSecuritySchemes(profileIdHeader, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(profileIdHeader)
                                .description("Job Seeker Profile ID (e.g. 1 for demo job seeker)")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(userIdHeader)
                        .addList(profileIdHeader));
    }
}
