package com.workus.workus.store.invite.application.model;

import java.time.LocalDateTime;

public record AcceptStoreInviteResult(
    Long storeId,
    String storeName,
    LocalDateTime acceptedAt
) {
}
