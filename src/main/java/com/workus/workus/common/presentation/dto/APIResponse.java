package com.workus.workus.common.presentation.dto;

public record APIResponse<S, E>(
	String code,
	String message,
	S data,
	E error
) {

	public static <S, E> APIResponse<S, E> ok(
		String code,
		String message,
		S data
	) {
		return new APIResponse<>(code, message, data, null);
	}

	public static <S, E> APIResponse<S, E> error(
		String code,
		String message,
		E error
	) {
		return new APIResponse<>(code, message, null, error);
	}
}
