package com.workus.workus.payroll.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * 귀속 기간 Value Object
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AccrualPeriod {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final WeekFields WEEK_FIELDS = WeekFields.of(Locale.KOREA);

    @Column(name = "accrual_start_date")
    private LocalDate startDate;

    @Column(name = "accrual_end_date")
    private LocalDate endDate;

    private AccrualPeriod(LocalDate startDate, LocalDate endDate) {
        validatePeriod(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // ========== 생성 메서드 ==========

    public static AccrualPeriod of(LocalDate startDate, LocalDate endDate) {
        return new AccrualPeriod(startDate, endDate);
    }

    /**
     * 월급용: 연월로 기간 생성 (1일 ~ 말일)
     */
    public static AccrualPeriod ofMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return new AccrualPeriod(startDate, endDate);
    }

    /**
     * 월급용: YearMonth로 기간 생성
     */
    public static AccrualPeriod ofMonth(YearMonth yearMonth) {
        return ofMonth(yearMonth.getYear(), yearMonth.getMonthValue());
    }

    /**
     * 일급용: 특정 날짜로 기간 생성
     */
    public static AccrualPeriod ofDay(LocalDate date) {
        return new AccrualPeriod(date, date);
    }

    /**
     * 주급용: 시작일로부터 1주일 기간 생성
     */
    public static AccrualPeriod ofWeek(LocalDate startDate) {
        return new AccrualPeriod(startDate, startDate.plusDays(6));
    }

    // ========== 연월 관련 ==========

    /**
     * 귀속 연월 (시작일 기준) - YYYYMM 형식
     */
    public String getYearMonth() {
        return startDate.format(YEAR_MONTH_FORMATTER);
    }

    /**
     * 귀속 연월 (시작일 기준) - YearMonth 객체
     */
    public YearMonth getYearMonthValue() {
        return YearMonth.from(startDate);
    }

    /**
     * 연도 (시작일 기준)
     */
    public int getYear() {
        return startDate.getYear();
    }

    /**
     * 월 (시작일 기준)
     */
    public int getMonth() {
        return startDate.getMonthValue();
    }

    /**
     * 연월 텍스트 - "2026년 2월"
     */
    public String getYearMonthText() {
        return startDate.getYear() + "년 " + startDate.getMonthValue() + "월";
    }

    // ========== 주차 관련 ==========

    /**
     * 월의 몇 주차인지 (시작일 기준)
     */
    public int getWeekOfMonth() {
        return startDate.get(WEEK_FIELDS.weekOfMonth());
    }

    /**
     * 주차 텍스트 - "2026년 2월 1주차"
     */
    public String getWeekText() {
        return getYearMonthText() + " " + getWeekOfMonth() + "주차";
    }

    /**
     * 연간 몇 주차인지 (시작일 기준)
     */
    public int getWeekOfYear() {
        return startDate.get(WEEK_FIELDS.weekOfYear());
    }

    // ========== 기간 관련 ==========

    /**
     * 귀속 기간 일수
     */
    public int getDays() {
        return (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
    }

    /**
     * 기간 텍스트 - "2026-02-01 ~ 2026-02-28"
     */
    public String getPeriodText() {
        return startDate.toString() + " ~ " + endDate.toString();
    }

    /**
     * 기간 텍스트 (한글) - "2026년 2월 1일 ~ 2026년 2월 28일"
     */
    public String getPeriodTextKorean() {
        return formatDateKorean(startDate) + " ~ " + formatDateKorean(endDate);
    }

    // ========== 포함 여부 확인 ==========

    /**
     * 특정 날짜가 기간에 포함되는지 확인
     */
    public boolean contains(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * 특정 연월에 해당하는 기간인지 확인
     */
    public boolean isInYearMonth(YearMonth yearMonth) {
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();
        return !endDate.isBefore(monthStart) && !startDate.isAfter(monthEnd);
    }

    /**
     * 특정 연월에 해당하는 기간인지 확인 (YYYYMM 문자열)
     */
    public boolean isInYearMonth(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, YEAR_MONTH_FORMATTER);
        return isInYearMonth(ym);
    }

    /**
     * 다른 기간과 겹치는지 확인
     */
    public boolean overlaps(AccrualPeriod other) {
        return !endDate.isBefore(other.startDate) && !startDate.isAfter(other.endDate);
    }

    // ========== 유틸 ==========

    private void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }
    }

    private String formatDateKorean(LocalDate date) {
        return date.getYear() + "년 " + date.getMonthValue() + "월 " + date.getDayOfMonth() + "일";
    }

    @Override
    public String toString() {
        return getPeriodText();
    }
}
