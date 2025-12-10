package com.workus.workus.schedule.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import java.util.Objects;

// [start, end)
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class TimeRange {
    private LocalTime start;
    private LocalTime end;

    public TimeRange(LocalTime start, LocalTime end) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        this.start = start;
        this.end = end;
    }

    public boolean spansNextDay() {
        return end.isBefore(start);
    }

    @Column
    public LocalTime start() {
        return start;
    }

    @Column
    public LocalTime end() {
        return end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TimeRange) obj;
        return Objects.equals(this.start, that.start) &&
                Objects.equals(this.end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "TimeRange[" +
                "start=" + start + ", " +
                "end=" + end + ']';
    }

}

