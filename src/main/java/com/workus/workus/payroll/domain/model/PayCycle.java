package com.workus.workus.payroll.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 급여 정산/지급 주기
 */
@Getter
@RequiredArgsConstructor
public enum PayCycle {
    DAILY("일급"), // 현재 미사용 - 추후 일용직 지원 시 활성화
    WEEKLY("주급"),// 현재 미사용 - 추후 주급제 지원 시 활성화
    MONTHLY("월급");

    private final String description;

    /**
     * 기준일(시작일)로부터 기본 종료일 계산
     *
     * @param startDate 기준 시작일
     * @return 기본 종료일
     */
    public LocalDate getDefaultEndDate(LocalDate startDate) {
        return switch (this) {
            case DAILY -> startDate;
            case WEEKLY -> startDate.plusDays(6);
            case MONTHLY -> startDate.withDayOfMonth(startDate.lengthOfMonth());
        };
    }

    /**
     * 기준일(시작일)로부터 기본 귀속 기간 생성
     *
     * @param startDate 기준 시작일
     * @return AccrualPeriod
     */
    public AccrualPeriod getDefaultPeriod(LocalDate startDate) {
        return switch (this) {
            case DAILY -> AccrualPeriod.ofDay(startDate);
            case WEEKLY -> AccrualPeriod.ofWeek(startDate);
            case MONTHLY -> AccrualPeriod.of(startDate, getDefaultEndDate(startDate));
        };
    }

    /**
     * 연월로부터 월급용 귀속 기간 생성 (1일 ~ 말일)
     *
     * @param year 연도
     * @param month 월
     * @return AccrualPeriod
     */
    public static AccrualPeriod getMonthlyPeriod(int year, int month) {
        return AccrualPeriod.ofMonth(year, month);
    }

    /**
     * YearMonth로부터 월급용 귀속 기간 생성
     *
     * @param yearMonth YearMonth
     * @return AccrualPeriod
     */
    public static AccrualPeriod getMonthlyPeriod(YearMonth yearMonth) {
        return AccrualPeriod.ofMonth(yearMonth);
    }
}
