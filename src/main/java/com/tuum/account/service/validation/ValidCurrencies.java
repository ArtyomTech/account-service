package com.tuum.account.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
@Documented
public @interface ValidCurrencies {
  String message() default "Invalid currency";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
