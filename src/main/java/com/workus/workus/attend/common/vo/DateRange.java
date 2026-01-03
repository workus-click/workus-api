package com.workus.workus.attend.common.vo;

import java.time.LocalDate;

// [from, to]
public record DateRange(LocalDate from, LocalDate to) {
    public DateRange {
        if (from == null || to == null) throw new IllegalArgumentException();
        if (to.isBefore(from)) throw new IllegalArgumentException();
    }
}