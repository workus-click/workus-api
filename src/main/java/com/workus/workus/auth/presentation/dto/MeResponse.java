package com.workus.workus.auth.presentation.dto;

import java.util.List;

public record MeResponse(
	Long userId,
	String loginId,
	String name,
	List<MeCompanyResponse> companies
) {
}
