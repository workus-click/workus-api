package com.workus.workus.auth.application.model;

public record LoginCompany(
	Long storeId,
	String storeName,
	String storeAddress
) {
}
