package com.workus.workus.schedule.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;

class TimeRangeTest {

    @Test
    void shouldCreateWhenNonNull() {
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        TimeRange range = new TimeRange(start, end);

        assertEquals(start, range.start());
        assertEquals(end, range.end());
    }

    @Test
    void constructorThrowsWhenStartNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new TimeRange(null, LocalTime.NOON));
        assertEquals("start must not be null", ex.getMessage());
    }

    @Test
    void constructorThrowsWhenEndNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new TimeRange(LocalTime.NOON, null));
        assertEquals("end must not be null", ex.getMessage());
    }
}