package tz.co.halotel.halokazi.common.error;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.Transactional;

/**
 * A transaction that still COMMITS when an {@link ApiException} is thrown.
 * <p>
 * An ApiException is an expected rejection (wrong password, wrong OTP, idle session...) and the
 * security bookkeeping written before it - failed-login counters, login_attempt and audit_log
 * rows, OTP attempt counts, session revocation - must survive it (AUTH-13). Services therefore
 * check before they mutate business data. Any other exception rolls back as usual.
 * <p>
 * Use it on every service that may throw ApiException inside a transaction: a plain
 * {@code @Transactional} participant would mark the shared transaction rollback-only.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transactional(noRollbackFor = ApiException.class)
public @interface BusinessTransaction {

    @AliasFor(annotation = Transactional.class, attribute = "readOnly")
    boolean readOnly() default false;
}
