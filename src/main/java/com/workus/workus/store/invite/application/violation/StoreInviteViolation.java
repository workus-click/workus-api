package com.workus.workus.store.invite.application.violation;

public sealed interface StoreInviteViolation permits
    StoreInviteViolation.InvalidToken,
    StoreInviteViolation.ExpiredInvite,
    StoreInviteViolation.AlreadyAccepted,
    StoreInviteViolation.IdentityMismatch,
    StoreInviteViolation.AlreadyMember,
    StoreInviteViolation.TechnicalFailure {

    record InvalidToken(String token) implements StoreInviteViolation {
    }

    record ExpiredInvite() implements StoreInviteViolation {
    }

    record AlreadyAccepted() implements StoreInviteViolation {
    }

    record IdentityMismatch() implements StoreInviteViolation {
    }

    record AlreadyMember() implements StoreInviteViolation {
    }

    record TechnicalFailure(String reason) implements StoreInviteViolation {
    }
}
