package tz.co.halotel.halokazi.common.security;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tz.co.halotel.halokazi.common.exception.BadRequestException;

@Component
public class UserPrincipalResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(UserPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
        boolean required = annotation != null && annotation.required();

        String userIdHeader = webRequest.getHeader("X-User-Id");
        String profileIdHeader = webRequest.getHeader("X-Profile-Id");

        if (userIdHeader != null && !userIdHeader.isBlank()) {
            Long userId = Long.valueOf(userIdHeader.trim());
            Long profileId = (profileIdHeader != null && !profileIdHeader.isBlank())
                    ? Long.valueOf(profileIdHeader.trim())
                    : userId; // fallback in mock environments

            return new UserPrincipal(userId, profileId, "Job Seeker", "seeker@halotel.co.tz", "JOB_SEEKER");
        }

        if (profileIdHeader != null && !profileIdHeader.isBlank()) {
            Long profileId = Long.valueOf(profileIdHeader.trim());
            return new UserPrincipal(profileId, profileId, "Job Seeker", "seeker@halotel.co.tz", "JOB_SEEKER");
        }

        // Development fallback: if not provided and not strictly required or in local dev
        if (!required) {
            return null;
        }

        // In development/test mode, provide a default seeded test user so frontend/Postman works out of the box
        // if headers are omitted, but log or permit standard test user id: 1
        return new UserPrincipal(1L, 1L, "Demo Job Seeker", "demo@halotel.co.tz", "JOB_SEEKER");
    }
}
