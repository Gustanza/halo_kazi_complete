package tz.co.halotel.halokazi.audit;

import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** LOGIN_ATTEMPT row (AUTH-13); attempted_at is left to the column default. */
@Entity
@Table(name = "login_attempt")
class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long id;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "succeeded", nullable = false)
    private boolean succeeded;

    @Column(name = "failure_reason")
    private String failureReason;

    protected LoginAttempt() {
    }

    LoginAttempt(String identifier, Long userId, String ipAddress, boolean succeeded, String failureReason) {
        this.identifier = identifier;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.succeeded = succeeded;
        this.failureReason = failureReason;
    }
}
