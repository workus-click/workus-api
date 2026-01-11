package com.workus.workus.common.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateValidator.class)
@Documented
public @interface IsoDate {

    String message() default "{date.format.iso}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


