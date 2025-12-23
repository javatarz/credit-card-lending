package me.karun.bank.credit.customer.internal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SsnValidator.class)
@Documented
public @interface ValidSsn {
    String message() default "Invalid SSN format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
