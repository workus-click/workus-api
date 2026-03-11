package com.workus.workus.store.invite.presentation.dto;

import java.time.LocalDateTime;

import com.workus.workus.store.invite.domain.model.InviteStatus;

public record ResolveStoreInviteResponse(
    String storeName,
    String employeeName,
    InviteStatus status,
    LocalDateTime expiresAt
) {
}
