package com.workus.workus.attend.common.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;

class TimeRangeTest {

    @Nested
    @DisplayName("시간범위 생성")
    class Creation{
        @Test
        @DisplayName("start와 end가 제공될 시 생성에 성공한다.")
        void shouldCreateWhenNonNull() {
            LocalTime start = LocalTime.of(9, 0);
            LocalTime end = LocalTime.of(17, 0);

            TimeRange range = new TimeRange(start, end);

            assertEquals(start, range.start());
            assertEquals(end, range.end());
        }

        @Test
        @DisplayName("start가 null일 시 생성되지 않는다.")
        void constructorThrowsWhenStartNull() {
            assertThrows(NullPointerException.class, () -> new TimeRange(null, LocalTime.NOON));
        }

        @Test
        @DisplayName("end가 null일 시 생성되지 않는다.")
        void constructorThrowsWhenEndNull() {
            assertThrows(NullPointerException.class, () -> new TimeRange(LocalTime.NOON, null));
        }
    }


    @Nested
    @DisplayName("익일종료 시간범위 판단")
    class SpansNextDay{
        @Test
        @DisplayName("start > end 이면 익일종료로 판단한다.")
        void spansNextDay_returnsTrueWhenEndBeforeStart() {
            TimeRange range = new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0));
            assertTrue(range.spansNextDay());
        }
        @Test
        @DisplayName("start = end 이면 익일종료로 판단한다.")
        void spansNextDay_returnsTrueWhenStartEqualsEnd() {
            TimeRange range = new TimeRange(LocalTime.of(22, 0), LocalTime.of(22, 0));
            assertTrue(range.spansNextDay());
        }
        @Test
        @DisplayName("start < end 이면 익일종료로 판단하지 않는다.")
        void spansNextDay_returnsFalseWhenStartBeforeEnd() {
            TimeRange range = new TimeRange(LocalTime.of(22, 0), LocalTime.of(23, 0));
            assertFalse(range.spansNextDay());
        }
    }


    @Nested
    @DisplayName("종일(24시간) 판단")
    class IsFullDay {
        @Test
        @DisplayName("start와 end가 동일하면, 24시간으로 간주한다.")
        void isFullDay_returnsTrueWhenStartEqualsEnd() {
            TimeRange range = new TimeRange(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
            assertTrue(range.isFullDay());
        }

        @Test
        @DisplayName("start와 end가 다를 시, 24시간이 아니다")
        void isFullDay_returnsFalseWhenStartNotEqualsEnd() {
            TimeRange range = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            assertFalse(range.isFullDay());
        }
    }


    @Nested
    @DisplayName("시간범위 포함여부 판단")
    class Contains {
        @Test
        @DisplayName("other의 시작이 this보다 이르면, this는 other를 포함하지 않는다.")
        void contains_returnsFalseWhenOtherStartsBefore() {
            TimeRange range = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            TimeRange other = new TimeRange(LocalTime.of(8, 30), LocalTime.of(12, 0));

            assertFalse(range.contains(other));
        }

        @Test
        @DisplayName("other의 종료가 this보다 늦으면, this는 other를 포함하지 않는다.")
        void contains_returnsFalseWhenOtherEndsAfter() {
            TimeRange range = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            TimeRange other = new TimeRange(LocalTime.of(16, 0), LocalTime.of(18, 0));

            assertFalse(range.contains(other));
        }

        @Test
        @DisplayName("other의 종료만 익일로 넘어갈 시, 종료시간의 대소와 무관하게 this는 other를 포함하지 않는다.")
        void contains_returnsFalseWhenOnlyOtherEndsNextDay() {
            TimeRange range = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            TimeRange other = new TimeRange(LocalTime.of(16, 0), LocalTime.of(2, 0));

            assertFalse(range.contains(other));
        }

        @Test
        @DisplayName("this와 other가 같은날이고, this.start <= other.start <= other.end <= this.end일 시, this는 other를 포함한다.")
        void contains_returnsTrueWhenOtherInsideSameDay() {
            TimeRange range = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));

            TimeRange otherWithin = new TimeRange(LocalTime.of(10, 0), LocalTime.of(15, 0));
            TimeRange otherWithSameStart = new TimeRange(range.start(), otherWithin.end());
            TimeRange otherWithSameEnd = new TimeRange(otherWithin.start(), range.end());
            TimeRange otherWithSameRange = new TimeRange(range.start(), range.end());

            assertTrue(range.contains(otherWithin));
            assertTrue(range.contains(otherWithSameStart));
            assertTrue(range.contains(otherWithSameEnd));
            assertTrue(range.contains(otherWithSameRange));
        }

        @Test
        @DisplayName("this와 other가 모두 익일에 종료되고, this.start <= other.start <= other.end <= this.end일 시, this는 other를 포함한다.")void contains_returnsTrueWhenBothSpanNextDay() {
            TimeRange range = new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0));

            TimeRange otherWithin = new TimeRange(LocalTime.of(23, 0), LocalTime.of(5, 0));
            TimeRange otherWithSameStart = new TimeRange(range.start(), otherWithin.end());
            TimeRange otherWithSameEnd = new TimeRange(otherWithin.start(), range.end());
            TimeRange otherWithSameRange = new TimeRange(range.start(), range.end());

            assertTrue(range.contains(otherWithin));
            assertTrue(range.contains(otherWithSameStart));
            assertTrue(range.contains(otherWithSameEnd));
            assertTrue(range.contains(otherWithSameRange));
        }

        @Test
        @DisplayName("this만 익일에 종료되고, this.start <= other.start일 시, this는 other를 포함한다.")
        void contains_returnsTrueWhenOnlyThisSpansNextDay() {
            TimeRange range = new TimeRange(LocalTime.of(9, 0), LocalTime.of(2, 0));

            TimeRange otherWithin = new TimeRange(LocalTime.of(10, 0), LocalTime.of(15, 0));
            TimeRange otherWithSameStart = new TimeRange(range.start(), otherWithin.end());

            assertTrue(range.contains(otherWithin));
            assertTrue(range.contains(otherWithSameStart));
        }
    }

}
