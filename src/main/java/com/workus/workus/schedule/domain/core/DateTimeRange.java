package com.workus.workus.schedule.domain.core;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

// [start, end)
public record DateTimeRange(LocalDateTime start, LocalDateTime end) {
    public DateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end는 start보다 이후여야 합니다.");
        }
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
