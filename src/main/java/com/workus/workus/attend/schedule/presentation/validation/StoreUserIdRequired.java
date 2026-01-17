package com.workus.workus.attend.schedule.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@NotNull
public @interface StoreUserIdRequired {

    /**
     * 이 message 값을 @NotNull.message 로 "전달(override)" 해줌
     */
    @OverridesAttribute(constraint = NotNull.class, name = "message")
    String message() default "{schedule.storeUserId.required}";

    @OverridesAttribute(constraint = NotNull.class, name = "groups")
    Class<?>[] groups() default {};

    @OverridesAttribute(constraint = NotNull.class, name = "payload")
    Class<? extends Payload>[] payload() default {};
}
