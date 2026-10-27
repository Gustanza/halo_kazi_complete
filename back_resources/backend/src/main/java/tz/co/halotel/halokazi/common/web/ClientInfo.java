package tz.co.halotel.halokazi.common.web;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Who is calling: client IP and user agent, sized to fit the audit/session columns.
 * Declare it as a controller method parameter to have it resolved automatically.
 */
public record ClientInfo(String ip, String userAgent) {

    private static final int MAX_IP_LENGTH = 45;
    private static final int MAX_USER_AGENT_LENGTH = 400;

    public static ClientInfo from(HttpServletRequest request) {
        return new ClientInfo(
                truncate(clientIp(request), MAX_IP_LENGTH),
                truncate(request.getHeader(HttpHeaders.USER_AGENT), MAX_USER_AGENT_LENGTH));
    }

    /** First hop of X-Forwarded-For (set by the reverse proxy), else the socket address. */
    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            String firstHop = forwarded.split(",", 2)[0].trim();
            if (!firstHop.isEmpty()) {
                return firstHop;
            }
        }
        return request.getRemoteAddr();
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max);
    }
}
