package tz.co.halotel.halokazi.session;

import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;

/**
 * Requires a valid {@code Authorization: Bearer <token>} on protected routes and stores the
 * caller for {@link CurrentUserArgumentResolver}. Errors propagate to the global handler.
 */
class SessionAuthInterceptor implements HandlerInterceptor {

    static final String ATTRIBUTE = AuthenticatedUser.class.getName();
    private static final String BEARER_PREFIX = "Bearer ";

    private final SessionService sessions;

    SessionAuthInterceptor(SessionService sessions) {
        this.sessions = sessions;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // CORS preflights carry no credentials; unmapped paths should 404, not 401.
        if (CorsUtils.isPreFlightRequest(request) || !(handler instanceof HandlerMethod)) {
            return true;
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        request.setAttribute(ATTRIBUTE, sessions.authenticate(token));
        return true;
    }
}
