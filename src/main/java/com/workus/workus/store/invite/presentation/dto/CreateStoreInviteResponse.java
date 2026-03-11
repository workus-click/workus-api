package com.workus.workus.store.invite.presentation.dto;

import java.time.LocalDateTime;

public record CreateStoreInviteResponse(
    String inviteToken,
    String inviteUrl,
    LocalDateTime expiresAt
) {
}
