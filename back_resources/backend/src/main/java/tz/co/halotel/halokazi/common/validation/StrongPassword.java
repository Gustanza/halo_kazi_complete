package tz.co.halotel.halokazi.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import tz.co.halotel.halokazi.common.security.PasswordPolicy;

/** Enforces {@link PasswordPolicy} (AUTH-06). The validator is a Spring-managed bean. Null is left to {@code @NotBlank}. */
@Documented
@Constraint(validatedBy = StrongPassword.Validator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<StrongPassword, String> {

        private final PasswordPolicy policy;

        public Validator(PasswordPolicy policy) {
            this.policy = policy;
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isEmpty() || policy.isSatisfiedBy(value)) {
                return true;
            }
            // The rule text includes the configured minimum length, so build it at runtime.
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(escape(policy.describe())).addConstraintViolation();
            return false;
        }

        private static String escape(String message) {
            return message.replace("\\", "\\\\").replace("{", "\\{").replace("}", "\\}").replace("$", "\\$");
        }
    }
}
