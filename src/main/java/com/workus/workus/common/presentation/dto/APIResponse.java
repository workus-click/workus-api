package com.workus.workus.common.presentation.dto;

public record APIResponse<S>(
	String code,
	String message,
	S data
) {

	public static <S> APIResponse<S> ok(
		String code,
		String message,
		S data
	) {
		return new APIResponse<>(code, message, data);
	}

	public static <E> APIResponse<E> error(
		String code,
		String message,
		E error
	) {
		return new APIResponse<>(code, message, error);
	}
}
