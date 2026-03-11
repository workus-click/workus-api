package com.workus.workus.store.invite.application.model;

import java.time.LocalDateTime;

public record CreateStoreInviteResult(
    String inviteToken,
    String inviteUrl,
    LocalDateTime expiresAt
) {
}
