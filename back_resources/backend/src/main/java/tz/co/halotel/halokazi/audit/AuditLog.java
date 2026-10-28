package tz.co.halotel.halokazi.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * AUDIT_LOG row. Append-only: a database trigger rejects UPDATE/DELETE, so the entity has no
 * mutators and created_at is left to the column default.
 */
@Entity
@Table(name = "audit_log")
class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long id;

    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Column(name = "actor_role", updatable = false)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, updatable = false)
    private AuditAction action;

    @Column(name = "entity_type", updatable = false)
    private String entityType;

    @Column(name = "entity_id", updatable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, updatable = false)
    private AuditResult result;

    @Column(name = "ip_address", updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", updatable = false)
    private String userAgent;

    @Lob
    @Column(name = "details", updatable = false)
    private String details;

    protected AuditLog() {
    }

    AuditLog(Long userId, String actorRole, AuditAction action, String entityType, Long entityId,
            AuditResult result, String ipAddress, String userAgent, String details) {
        this.userId = userId;
        this.actorRole = actorRole;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.result = result;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.details = details;
    }
}
