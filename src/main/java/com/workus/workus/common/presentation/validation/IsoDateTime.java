package com.workus.workus.common.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateTimeValidator.class)
@Documented
public @interface IsoDateTime {

    String message() default "{datetime.format.iso}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

