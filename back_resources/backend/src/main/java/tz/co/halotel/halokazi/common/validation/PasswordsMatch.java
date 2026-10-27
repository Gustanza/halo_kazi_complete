package tz.co.halotel.halokazi.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

/** Class-level check that {@code repeatPassword} equals {@code password}; reported on the repeatPassword field. */
@Documented
@Constraint(validatedBy = PasswordsMatch.Validator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatch {

    String message() default "Passwords do not match.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Implemented by request bodies that carry a password and its confirmation. */
    interface PasswordConfirmation {
        String password();

        String repeatPassword();
    }

    class Validator implements ConstraintValidator<PasswordsMatch, PasswordConfirmation> {
        @Override
        public boolean isValid(PasswordConfirmation value, ConstraintValidatorContext context) {
            if (value == null || Objects.equals(value.password(), value.repeatPassword())) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("repeatPassword")
                    .addConstraintViolation();
            return false;
        }
    }
}
