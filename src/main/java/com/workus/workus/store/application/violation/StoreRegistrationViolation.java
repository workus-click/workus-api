package com.workus.workus.store.application.violation;

public sealed interface StoreRegistrationViolation permits
    StoreRegistrationViolation.DuplicateBusinessNumber,
    StoreRegistrationViolation.TechnicalFailure {

    record DuplicateBusinessNumber(String businessNumber) implements StoreRegistrationViolation {
    }

    record TechnicalFailure(String reason) implements StoreRegistrationViolation {
    }
}
