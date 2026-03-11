package com.workus.workus.store.invite.presentation.dto;

import java.time.LocalDateTime;

public record AcceptStoreInviteResponse(
    Long storeId,
    String storeName,
    LocalDateTime acceptedAt
) {
}
