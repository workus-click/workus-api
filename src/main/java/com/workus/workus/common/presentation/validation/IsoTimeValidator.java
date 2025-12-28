package com.workus.workus.common.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class IsoTimeValidator
        implements ConstraintValidator<IsoTime, String> {

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        try {
            LocalTime.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
