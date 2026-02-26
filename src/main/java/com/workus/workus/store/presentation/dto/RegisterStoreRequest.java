package com.workus.workus.store.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterStoreRequest(
    @NotBlank
    String storeName,

    @NotBlank
    String businessNumber,

    @NotBlank
    String representativeName,

    @NotBlank
    String businessType,

    @NotBlank
    String contactPhoneNumber,

    @NotBlank
    String residentNumber,

    @NotBlank
    String storeAddress
) {
}
