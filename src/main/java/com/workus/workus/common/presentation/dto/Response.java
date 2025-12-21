package com.workus.workus.common.presentation.dto;

public record Response<T>(
        long code,
        String message,
    T data
) {
    public static <T> Response<T> of(long code, String message, T data) {
        return new Response<>(code, message, data);
    }
}
