package com.workus.workus.attend.common.vo;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalTime;

import java.util.Objects;

// [start, end)
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class TimeRange {
    private static final int DAY_SECONDS = 24 * 60 * 60;

    private LocalTime start;
    private LocalTime end;

    public TimeRange(@NonNull LocalTime start, @NonNull LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean spansNextDay() {
        return !end.isAfter(start);
    }
    boolean isFullDay() {
        return start.equals(end);
    }

    /**
     * this 범위가 other 범위를 완전히 포함하는지 여부.
     * 두 TimeRange의 start를 동일날짜로 간주한다.
     */
    public boolean contains(@NonNull TimeRange other) {
        return this.start.toSecondOfDay() <= other.start.toSecondOfDay()
                && other.endOffset() <= this.endOffset();
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

    private int endOffset() {
        return this.end.toSecondOfDay() + (spansNextDay() || isFullDay() ? DAY_SECONDS : 0);
    }
}