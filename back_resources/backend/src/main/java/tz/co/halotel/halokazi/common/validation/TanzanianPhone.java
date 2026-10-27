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
import tz.co.halotel.halokazi.common.util.PhoneNumbers;

/** A Tanzanian mobile number in any format {@link PhoneNumbers} accepts. Null is left to {@code @NotBlank}. */
@Documented
@Constraint(validatedBy = TanzanianPhone.Validator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface TanzanianPhone {

    String message() default "Enter a valid Tanzanian mobile number, e.g. 7XX XXX XXX.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<TanzanianPhone, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || value.isBlank() || PhoneNumbers.isValid(value);
        }
    }
}
