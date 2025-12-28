package com.workus.workus.schedule.domain.core;

import java.time.LocalDate;

// [fromInclusive, toInclusive]
public record DateRange(LocalDate fromInclusive, LocalDate toInclusive) {
    public DateRange {
        if (fromInclusive == null || toInclusive == null) throw new IllegalArgumentException();
        if (toInclusive.isBefore(fromInclusive)) throw new IllegalArgumentException();
    }
}