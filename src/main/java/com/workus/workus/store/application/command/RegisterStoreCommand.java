package com.workus.workus.store.application.command;

public record RegisterStoreCommand(
    String storeName,
    String businessNumber,
    String representativeName,
    String businessType,
    String contactPhoneNumber,
    String storeZipCode,
    String storeAddress
) {
}
