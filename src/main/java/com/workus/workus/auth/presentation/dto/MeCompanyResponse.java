package com.workus.workus.auth.presentation.dto;

public record MeCompanyResponse(
	Long storeId,
	String storeName,
	String storeAddress
) {
}
