package com.workus.workus.attend.common.vo;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

// [start, end)
public record DateTimeRange(@NonNull LocalDateTime start, @NonNull LocalDateTime end) {
    public DateTimeRange {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end는 start보다 이후여야 합니다.");
        }
    }
    public static DateTimeRange of(LocalDate date, TimeRange timeRange){
        return new DateTimeRange(
                LocalDateTime.of(date, timeRange.start()),
                LocalDateTime.of(timeRange.spansNextDay() ? date.plusDays(1) : date, timeRange.end())
        );
    }

    public boolean isWithinRange(DateTimeRange other){
        return (other.start.isEqual(this.start) || other.start.isBefore(this.start))
                && (this.end.isEqual(other.end) || this.end.isBefore(other.end));
    }
    public boolean overlaps(DateTimeRange other) {
        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    public LocalTime getDuration(){
        return LocalTime.ofNanoOfDay(end.toLocalTime().toNanoOfDay() - start.toLocalTime().toNanoOfDay());
    }
}