package com.workus.workus.payroll.employee.domain.service;

import com.workus.workus.payroll.employee.domain.model.PayType;
import com.workus.workus.payroll.employee.presentation.dto.PayValidationRequest;
import com.workus.workus.payroll.employee.presentation.dto.PayValidationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 급여 유효성 검사 서비스
 */
@Service
public class PayValidationService {

    // TODO: 추후 DB에서 관리할 수 있음
    private static final BigDecimal MINIMUM_HOURLY_RATE = BigDecimal.valueOf(10320);
    private static final int WEEKS_PER_MONTH = 4;

    /**
     * 급여 유효성 검사
     */
    public PayValidationResponse validate(PayValidationRequest request) {
        return switch (request.payType()) {
            case HOURLY -> validateHourlyPay(request);
            case MONTHLY -> validateMonthlyPay(request);
            default -> throw new IllegalArgumentException("지원하지 않는 급여 형태입니다: " + request.payType());
        };
    }

    /**
     * 시급제 유효성 검사
     */
    private PayValidationResponse validateHourlyPay(PayValidationRequest request) {
        if (request.hourlyRate() == null) {
            return PayValidationResponse.failure(
                    "시급제의 경우 시급을 입력해야 합니다.",
                    request.payType(),
                    null,
                    null,
                    MINIMUM_HOURLY_RATE,
                    request.workHoursPerDay(),
                    request.workDaysPerWeek(),
                    calculateMonthlyWorkHours(request)
            );
        }

        BigDecimal hourlyRate = request.hourlyRate();
        int monthlyWorkHours = calculateMonthlyWorkHours(request);
        BigDecimal monthlySalary = hourlyRate.multiply(BigDecimal.valueOf(monthlyWorkHours));

        // 최저시급 검사
        if (hourlyRate.compareTo(MINIMUM_HOURLY_RATE) < 0) {
            return PayValidationResponse.failure(
                    String.format("시급은 최저시급(%s원) 이상이어야 합니다. 입력 시급: %s원",
                            MINIMUM_HOURLY_RATE.toPlainString(),
                            hourlyRate.setScale(0, RoundingMode.FLOOR).toPlainString()),
                    request.payType(),
                    hourlyRate,
                    monthlySalary,
                    MINIMUM_HOURLY_RATE,
                    request.workHoursPerDay(),
                    request.workDaysPerWeek(),
                    monthlyWorkHours
            );
        }

        return PayValidationResponse.success(
                request.payType(),
                hourlyRate,
                monthlySalary,
                MINIMUM_HOURLY_RATE,
                request.workHoursPerDay(),
                request.workDaysPerWeek(),
                monthlyWorkHours
        );
    }

    /**
     * 월급제 유효성 검사
     */
    private PayValidationResponse validateMonthlyPay(PayValidationRequest request) {
        if (request.monthlySalary() == null) {
            return PayValidationResponse.failure(
                    "월급제의 경우 월급을 입력해야 합니다.",
                    request.payType(),
                    null,
                    null,
                    MINIMUM_HOURLY_RATE,
                    request.workHoursPerDay(),
                    request.workDaysPerWeek(),
                    calculateMonthlyWorkHours(request)
            );
        }

        BigDecimal monthlySalary = request.monthlySalary();
        int monthlyWorkHours = calculateMonthlyWorkHours(request);

        if (monthlyWorkHours == 0) {
            return PayValidationResponse.failure(
                    "월 근무시간을 계산할 수 없습니다. 근무시간을 확인해주세요.",
                    request.payType(),
                    BigDecimal.ZERO,
                    monthlySalary,
                    MINIMUM_HOURLY_RATE,
                    request.workHoursPerDay(),
                    request.workDaysPerWeek(),
                    monthlyWorkHours
            );
        }

        BigDecimal hourlyRate = monthlySalary.divide(
                BigDecimal.valueOf(monthlyWorkHours), 2, RoundingMode.FLOOR);

        // 최저시급 검사
        if (hourlyRate.compareTo(MINIMUM_HOURLY_RATE) < 0) {
            return PayValidationResponse.failure(
                    String.format("환산 시급이 최저시급(%s원) 미만입니다. 환산 시급: %s원",
                            MINIMUM_HOURLY_RATE.toPlainString(),
                            hourlyRate.setScale(0, RoundingMode.FLOOR).toPlainString()),
                    request.payType(),
                    hourlyRate,
                    monthlySalary,
                    MINIMUM_HOURLY_RATE,
                    request.workHoursPerDay(),
                    request.workDaysPerWeek(),
                    monthlyWorkHours
            );
        }

        return PayValidationResponse.success(
                request.payType(),
                hourlyRate,
                monthlySalary,
                MINIMUM_HOURLY_RATE,
                request.workHoursPerDay(),
                request.workDaysPerWeek(),
                monthlyWorkHours
        );
    }

    /**
     * 월 총 근무시간 계산
     */
    private int calculateMonthlyWorkHours(PayValidationRequest request) {
        if (request.workHoursPerDay() == null || request.workDaysPerWeek() == null) {
            return 0;
        }
        return request.workHoursPerDay() * request.workDaysPerWeek() * WEEKS_PER_MONTH;
    }
}
