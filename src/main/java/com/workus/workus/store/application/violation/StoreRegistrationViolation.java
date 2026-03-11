package com.workus.workus.store.application.violation;

public sealed interface StoreRegistrationViolation permits
    StoreRegistrationViolation.TechnicalFailure {

    record TechnicalFailure(String reason) implements StoreRegistrationViolation {
    }
}
