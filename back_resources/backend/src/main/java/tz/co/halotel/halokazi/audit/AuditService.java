package tz.co.halotel.halokazi.audit;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.ObjectMapper;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.user.AppUser;

/**
 * Writes AUDIT_LOG rows in the caller's transaction. Details must never contain secrets
 * (passwords, OTP codes, tokens).
 */
@Service
public class AuditService {

    private static final String USER_ENTITY = "APP_USER";

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /** An action performed by (or on) {@code user}; pass null when the user is unknown. */
    @Transactional
    public void record(AuditAction action, AuditResult result, AppUser user, ClientInfo client,
            Map<String, ?> details) {
        Long userId = user == null ? null : user.getId();
        String role = user == null ? null : user.getRole().name();
        repository.save(new AuditLog(userId, role, action, userId == null ? null : USER_ENTITY, userId, result,
                client == null ? null : client.ip(),
                client == null ? null : client.userAgent(),
                toJson(details)));
    }

    public void success(AuditAction action, AppUser user, ClientInfo client) {
        record(action, AuditResult.SUCCESS, user, client, null);
    }

    private String toJson(Map<String, ?> details) {
        return details == null || details.isEmpty() ? null : objectMapper.writeValueAsString(details);
    }
}
