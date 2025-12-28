package com.workus.workus.schedule.domain.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeRangeTest {

    @Test
    void constructorRejectsEndBeforeOrEqualStart() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime endEqual = LocalDateTime.of(2025, 1, 1, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> new DateTimeRange(start, endEqual));

        LocalDateTime endBefore = LocalDateTime.of(2025, 1, 1, 9, 0);
        assertThrows(IllegalArgumentException.class, () -> new DateTimeRange(start, endBefore));
    }

    @Test
    void isWithinRange_trueAndFalse() {
        DateTimeRange inner = new DateTimeRange(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 11, 0)
        );
        DateTimeRange outer = new DateTimeRange(
                LocalDateTime.of(2025, 1, 1, 9, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0)
        );

        assertTrue(inner.isWithinRange(outer));
        assertFalse(outer.isWithinRange(inner));
    }

    @Test
    void overlaps_trueAndFalse() {
        DateTimeRange a = new DateTimeRange(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 11, 0)
        );
        DateTimeRange b = new DateTimeRange(
                LocalDateTime.of(2025, 1, 1, 10, 30),
                LocalDateTime.of(2025, 1, 1, 11, 30)
        );
        assertTrue(a.overlaps(b));
        assertTrue(b.overlaps(a));

        DateTimeRange c = new DateTimeRange(
                LocalDateTime.of(2025, 1, 1, 11, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0)
        );
        assertFalse(a.overlaps(c));
        assertFalse(c.overlaps(a));
    }

    @Test
    void getDuration_returnsLocalTimeDifference() {
        DateTimeRange r = new DateTimeRange(
                LocalDateTime.of(2025, 1, 1, 9, 15),
                LocalDateTime.of(2025, 1, 1, 10, 45)
        );
        LocalTime duration = r.getDuration();
        assertEquals(LocalTime.of(1, 30), duration);
    }
}