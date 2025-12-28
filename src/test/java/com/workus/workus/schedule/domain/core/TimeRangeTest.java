package com.workus.workus.schedule.domain.core;

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
        assertThrows(NullPointerException.class, () -> new TimeRange(null, LocalTime.NOON));
    }

    @Test
    void constructorThrowsWhenEndNull() {
        assertThrows(NullPointerException.class, () -> new TimeRange(LocalTime.NOON, null));
    }

    @Test
    void spansNextDay_returnsTrueWhenEndBeforeStart() {
        TimeRange range = new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0));
        assertTrue(range.spansNextDay());
    }
}