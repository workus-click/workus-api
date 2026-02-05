package com.workus.workus.payroll.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AccrualPeriod VO 테스트")
class AccrualPeriodTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("of - 시작일과 종료일로 생성")
        void of_success() {
            // given
            LocalDate startDate = LocalDate.of(2026, 2, 1);
            LocalDate endDate = LocalDate.of(2026, 2, 28);

            // when
            AccrualPeriod period = AccrualPeriod.of(startDate, endDate);

            // then
            assertThat(period.getStartDate()).isEqualTo(startDate);
            assertThat(period.getEndDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("of - 시작일이 종료일보다 늦으면 예외")
        void of_invalidPeriod_throwsException() {
            // given
            LocalDate startDate = LocalDate.of(2026, 2, 28);
            LocalDate endDate = LocalDate.of(2026, 2, 1);

            // when & then
            assertThatThrownBy(() -> AccrualPeriod.of(startDate, endDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("시작일은 종료일보다 이전이어야 합니다");
        }

        @Test
        @DisplayName("of - null 값이면 예외")
        void of_nullValue_throwsException() {
            assertThatThrownBy(() -> AccrualPeriod.of(null, LocalDate.now()))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> AccrualPeriod.of(LocalDate.now(), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("ofMonth - 연월로 월급 기간 생성 (1일~말일)")
        void ofMonth_success() {
            // when
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // then
            assertThat(period.getStartDate()).isEqualTo(LocalDate.of(2026, 2, 1));
            assertThat(period.getEndDate()).isEqualTo(LocalDate.of(2026, 2, 28));
        }

        @Test
        @DisplayName("ofMonth - 윤년 2월")
        void ofMonth_leapYear() {
            // when (2024년은 윤년)
            AccrualPeriod period = AccrualPeriod.ofMonth(2024, 2);

            // then
            assertThat(period.getEndDate()).isEqualTo(LocalDate.of(2024, 2, 29));
        }

        @Test
        @DisplayName("ofMonth - YearMonth로 생성")
        void ofMonth_withYearMonth() {
            // given
            YearMonth yearMonth = YearMonth.of(2026, 3);

            // when
            AccrualPeriod period = AccrualPeriod.ofMonth(yearMonth);

            // then
            assertThat(period.getStartDate()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(period.getEndDate()).isEqualTo(LocalDate.of(2026, 3, 31));
        }

        @Test
        @DisplayName("ofDay - 일급용 단일 날짜 기간")
        void ofDay_success() {
            // given
            LocalDate date = LocalDate.of(2026, 2, 15);

            // when
            AccrualPeriod period = AccrualPeriod.ofDay(date);

            // then
            assertThat(period.getStartDate()).isEqualTo(date);
            assertThat(period.getEndDate()).isEqualTo(date);
            assertThat(period.getDays()).isEqualTo(1);
        }

        @Test
        @DisplayName("ofWeek - 주급용 1주일 기간")
        void ofWeek_success() {
            // given
            LocalDate startDate = LocalDate.of(2026, 2, 1);

            // when
            AccrualPeriod period = AccrualPeriod.ofWeek(startDate);

            // then
            assertThat(period.getStartDate()).isEqualTo(startDate);
            assertThat(period.getEndDate()).isEqualTo(LocalDate.of(2026, 2, 7));
            assertThat(period.getDays()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("연월 관련 테스트")
    class YearMonthTest {

        @Test
        @DisplayName("getYearMonth - YYYYMM 형식 반환")
        void getYearMonth() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.getYearMonth()).isEqualTo("202602");
        }

        @Test
        @DisplayName("getYearMonthValue - YearMonth 객체 반환")
        void getYearMonthValue() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.getYearMonthValue()).isEqualTo(YearMonth.of(2026, 2));
        }

        @Test
        @DisplayName("getYear, getMonth - 연도와 월 반환")
        void getYearAndMonth() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.getYear()).isEqualTo(2026);
            assertThat(period.getMonth()).isEqualTo(2);
        }

        @Test
        @DisplayName("getYearMonthText - 한글 연월 텍스트")
        void getYearMonthText() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.getYearMonthText()).isEqualTo("2026년 2월");
        }
    }

    @Nested
    @DisplayName("주차 관련 테스트")
    class WeekTest {

        @Test
        @DisplayName("getWeekOfMonth - 월의 몇 주차인지")
        void getWeekOfMonth() {
            // given - 2026년 2월 1일은 일요일
            AccrualPeriod firstWeek = AccrualPeriod.ofWeek(LocalDate.of(2026, 2, 1));
            AccrualPeriod secondWeek = AccrualPeriod.ofWeek(LocalDate.of(2026, 2, 8));

            // when & then
            assertThat(firstWeek.getWeekOfMonth()).isEqualTo(1);
            assertThat(secondWeek.getWeekOfMonth()).isEqualTo(2);
        }

        @Test
        @DisplayName("getWeekText - 주차 텍스트")
        void getWeekText() {
            // given
            AccrualPeriod period = AccrualPeriod.ofWeek(LocalDate.of(2026, 2, 1));

            // when & then
            assertThat(period.getWeekText()).isEqualTo("2026년 2월 1주차");
        }
    }

    @Nested
    @DisplayName("기간 관련 테스트")
    class PeriodTest {

        @Test
        @DisplayName("getDays - 기간 일수 계산")
        void getDays() {
            // given
            AccrualPeriod monthly = AccrualPeriod.ofMonth(2026, 2);  // 28일
            AccrualPeriod weekly = AccrualPeriod.ofWeek(LocalDate.of(2026, 2, 1));  // 7일
            AccrualPeriod daily = AccrualPeriod.ofDay(LocalDate.of(2026, 2, 1));  // 1일

            // when & then
            assertThat(monthly.getDays()).isEqualTo(28);
            assertThat(weekly.getDays()).isEqualTo(7);
            assertThat(daily.getDays()).isEqualTo(1);
        }

        @Test
        @DisplayName("getPeriodText - 기간 텍스트 (ISO 형식)")
        void getPeriodText() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.getPeriodText()).isEqualTo("2026-02-01 ~ 2026-02-28");
        }

        @Test
        @DisplayName("getPeriodTextKorean - 기간 텍스트 (한글)")
        void getPeriodTextKorean() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.getPeriodTextKorean()).isEqualTo("2026년 2월 1일 ~ 2026년 2월 28일");
        }

        @Test
        @DisplayName("toString - 기간 텍스트 반환")
        void toStringTest() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.toString()).isEqualTo("2026-02-01 ~ 2026-02-28");
        }
    }

    @Nested
    @DisplayName("포함 여부 테스트")
    class ContainsTest {

        @Test
        @DisplayName("contains - 특정 날짜가 기간에 포함되는지")
        void contains() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.contains(LocalDate.of(2026, 2, 1))).isTrue();   // 시작일
            assertThat(period.contains(LocalDate.of(2026, 2, 15))).isTrue();  // 중간
            assertThat(period.contains(LocalDate.of(2026, 2, 28))).isTrue();  // 종료일
            assertThat(period.contains(LocalDate.of(2026, 1, 31))).isFalse(); // 이전
            assertThat(period.contains(LocalDate.of(2026, 3, 1))).isFalse();  // 이후
        }

        @Test
        @DisplayName("isInYearMonth - 특정 연월에 해당하는지 (YearMonth)")
        void isInYearMonth_withYearMonth() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.isInYearMonth(YearMonth.of(2026, 2))).isTrue();
            assertThat(period.isInYearMonth(YearMonth.of(2026, 1))).isFalse();
            assertThat(period.isInYearMonth(YearMonth.of(2026, 3))).isFalse();
        }

        @Test
        @DisplayName("isInYearMonth - 특정 연월에 해당하는지 (문자열)")
        void isInYearMonth_withString() {
            // given
            AccrualPeriod period = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period.isInYearMonth("202602")).isTrue();
            assertThat(period.isInYearMonth("202601")).isFalse();
        }

        @Test
        @DisplayName("isInYearMonth - 월을 걸치는 기간")
        void isInYearMonth_crossMonth() {
            // given - 1월 25일 ~ 2월 5일
            AccrualPeriod period = AccrualPeriod.of(
                    LocalDate.of(2026, 1, 25),
                    LocalDate.of(2026, 2, 5)
            );

            // when & then
            assertThat(period.isInYearMonth(YearMonth.of(2026, 1))).isTrue();
            assertThat(period.isInYearMonth(YearMonth.of(2026, 2))).isTrue();
            assertThat(period.isInYearMonth(YearMonth.of(2026, 3))).isFalse();
        }

        @Test
        @DisplayName("overlaps - 다른 기간과 겹치는지")
        void overlaps() {
            // given
            AccrualPeriod feb = AccrualPeriod.ofMonth(2026, 2);
            AccrualPeriod jan = AccrualPeriod.ofMonth(2026, 1);
            AccrualPeriod mar = AccrualPeriod.ofMonth(2026, 3);
            AccrualPeriod crossMonth = AccrualPeriod.of(
                    LocalDate.of(2026, 1, 25),
                    LocalDate.of(2026, 2, 5)
            );

            // when & then
            assertThat(feb.overlaps(jan)).isFalse();        // 1월과 2월은 안 겹침
            assertThat(feb.overlaps(mar)).isFalse();        // 2월과 3월은 안 겹침
            assertThat(feb.overlaps(crossMonth)).isTrue();  // 1/25~2/5와 2월은 겹침
            assertThat(jan.overlaps(crossMonth)).isTrue();  // 1/25~2/5와 1월은 겹침
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualsTest {

        @Test
        @DisplayName("같은 시작일과 종료일이면 동등")
        void equals_samePeriod() {
            // given
            AccrualPeriod period1 = AccrualPeriod.ofMonth(2026, 2);
            AccrualPeriod period2 = AccrualPeriod.ofMonth(2026, 2);

            // when & then
            assertThat(period1).isEqualTo(period2);
            assertThat(period1.hashCode()).isEqualTo(period2.hashCode());
        }

        @Test
        @DisplayName("다른 기간이면 동등하지 않음")
        void equals_differentPeriod() {
            // given
            AccrualPeriod period1 = AccrualPeriod.ofMonth(2026, 2);
            AccrualPeriod period2 = AccrualPeriod.ofMonth(2026, 3);

            // when & then
            assertThat(period1).isNotEqualTo(period2);
        }
    }
}
