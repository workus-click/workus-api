package com.workus.workus.payroll.employee.presentation.dto;

import com.workus.workus.payroll.employee.domain.model.PayType;

import java.math.BigDecimal;

/**
 * 급여 유효성 검사 응답
 */
public record PayValidationResponse(
        boolean valid,              // 유효성 통과 여부
        String message,             // 결과 메시지
        PayType payType,            // 급여 형태
        BigDecimal hourlyRate,      // 시급 (환산 시급 포함)
        BigDecimal monthlySalary,   // 월급 (환산 월급 포함)
        BigDecimal minimumHourlyRate,  // 최저시급 기준
        Integer workHoursPerDay,    // 일 근무시간
        Integer workDaysPerWeek,    // 주 근무일수
        Integer monthlyWorkHours    // 월 총 근무시간
) {
    public static PayValidationResponse success(
            PayType payType,
            BigDecimal hourlyRate,
            BigDecimal monthlySalary,
            BigDecimal minimumHourlyRate,
            Integer workHoursPerDay,
            Integer workDaysPerWeek,
            Integer monthlyWorkHours) {
        return new PayValidationResponse(
                true,
                "유효한 급여입니다.",
                payType,
                hourlyRate,
                monthlySalary,
                minimumHourlyRate,
                workHoursPerDay,
                workDaysPerWeek,
                monthlyWorkHours
        );
    }

    public static PayValidationResponse failure(
            String message,
            PayType payType,
            BigDecimal hourlyRate,
            BigDecimal monthlySalary,
            BigDecimal minimumHourlyRate,
            Integer workHoursPerDay,
            Integer workDaysPerWeek,
            Integer monthlyWorkHours) {
        return new PayValidationResponse(
                false,
                message,
                payType,
                hourlyRate,
                monthlySalary,
                minimumHourlyRate,
                workHoursPerDay,
                workDaysPerWeek,
                monthlyWorkHours
        );
    }
}
