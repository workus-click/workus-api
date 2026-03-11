package com.workus.workus.store.invite.application.model;

import java.time.LocalDateTime;

import com.workus.workus.store.invite.domain.model.InviteStatus;

public record ResolveStoreInviteResult(
    String storeName,
    String employeeName,
    InviteStatus status,
    LocalDateTime expiresAt
) {
}
