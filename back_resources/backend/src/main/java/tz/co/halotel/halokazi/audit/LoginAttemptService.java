package tz.co.halotel.halokazi.audit;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.user.AppUser;

/** Records every login attempt in LOGIN_ATTEMPT plus the matching LOGIN_SUCCESS / LOGIN_FAILED audit row. */
@Service
public class LoginAttemptService {

    private static final int MAX_IDENTIFIER_LENGTH = 254;

    private final LoginAttemptRepository repository;
    private final AuditService audit;

    public LoginAttemptService(LoginAttemptRepository repository, AuditService audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional
    public void succeeded(String identifier, AppUser user, ClientInfo client) {
        repository.save(new LoginAttempt(clip(identifier), user.getId(), client.ip(), true, null));
        audit.success(AuditAction.LOGIN_SUCCESS, user, client);
    }

    /** @param user the matched account, or null when the identifier is unknown */
    @Transactional
    public void failed(String identifier, AppUser user, LoginFailureReason reason, ClientInfo client) {
        repository.save(new LoginAttempt(clip(identifier), user == null ? null : user.getId(), client.ip(), false,
                reason.name()));
        AuditResult result = reason == LoginFailureReason.BAD_PASSWORD || reason == LoginFailureReason.UNKNOWN_IDENTIFIER
                ? AuditResult.FAILURE
                : AuditResult.DENIED;
        audit.record(AuditAction.LOGIN_FAILED, result, user, client, Map.of("reason", reason.name()));
    }

    private static String clip(String identifier) {
        String value = identifier == null ? "" : identifier.trim();
        return value.length() <= MAX_IDENTIFIER_LENGTH ? value : value.substring(0, MAX_IDENTIFIER_LENGTH);
    }
}
