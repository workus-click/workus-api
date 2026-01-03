package com.workus.workus.attend.common.vo;

import lombok.NonNull;

import java.time.LocalDate;

// [start, end)
public record DateRange(@NonNull LocalDate start, @NonNull LocalDate end) {
    public DateRange {
        if (!end.isAfter(start)) throw new IllegalArgumentException("end는 start보다 이후여야 합니다.");
    }
}