package com.workus.workus.payroll.employee.presentation.dto;

import com.workus.workus.payroll.employee.domain.model.PayType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 급여 유효성 검사 요청
 */
public record PayValidationRequest(
        @NotNull(message = "급여 형태는 필수입니다")
        PayType payType,

        // 시급제인 경우 시급
        BigDecimal hourlyRate,

        // 월급제인 경우 월급
        BigDecimal monthlySalary,

        @NotNull(message = "일 근무시간은 필수입니다")
        @Min(value = 1, message = "일 근무시간은 1시간 이상이어야 합니다")
        Integer workHoursPerDay,

        @NotNull(message = "주 근무일수는 필수입니다")
        @Min(value = 1, message = "주 근무일수는 1일 이상이어야 합니다")
        Integer workDaysPerWeek
) {
}
