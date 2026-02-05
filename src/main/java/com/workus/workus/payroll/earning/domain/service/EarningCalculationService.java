package com.workus.workus.payroll.earning.domain.service;

import com.workus.workus.payroll.earning.domain.model.EarningAmount;
import com.workus.workus.payroll.earning.domain.model.EarningCalculationResult;
import com.workus.workus.payroll.earning.domain.model.EarningType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

/**
 * 지급항목 계산 서비스
 * - 기본급, 각종 수당, 상여 계산
 */
@Service
@RequiredArgsConstructor
public class EarningCalculationService {

    // 법정 가산율
    private static final BigDecimal OVERTIME_RATE = new BigDecimal("1.5");      // 연장근로 150%
    private static final BigDecimal NIGHT_SHIFT_RATE = new BigDecimal("0.5");   // 야간근로 50% 가산
    private static final BigDecimal HOLIDAY_RATE = new BigDecimal("1.5");       // 휴일근로 150%

    /**
     * 지급항목 계산
     *
     * @param baseSalary 기본급 (시급 또는 월급)
     * @param hourlyRate 시급 (수당 계산용)
     * @param overtimeHours 연장근로시간
     * @param nightShiftHours 야간근로시간
     * @param holidayHours 휴일근로시간
     * @param weeklyHolidayHours 주휴시간
     * @param additionalEarnings 추가 지급항목 (식대, 차량유지비 등)
     * @return 지급항목 계산 결과
     */
    public EarningCalculationResult calculate(
            BigDecimal baseSalary,
            BigDecimal hourlyRate,
            double overtimeHours,
            double nightShiftHours,
            double holidayHours,
            double weeklyHolidayHours,
            Map<EarningType, BigDecimal> additionalEarnings) {

        Map<EarningType, EarningAmount> details = new EnumMap<>(EarningType.class);

        // 기본급
        details.put(EarningType.BASE_SALARY, EarningAmount.of(EarningType.BASE_SALARY, baseSalary));

        // 연장근로수당 = 시급 × 1.5 × 연장근로시간
        BigDecimal overtimeAllowance = calculateAllowance(hourlyRate, OVERTIME_RATE, overtimeHours);
        details.put(EarningType.OVERTIME_ALLOWANCE,
                overtimeHours > 0
                        ? EarningAmount.of(EarningType.OVERTIME_ALLOWANCE, overtimeAllowance)
                        : EarningAmount.notApplicable(EarningType.OVERTIME_ALLOWANCE));

        // 야간근로수당 = 시급 × 0.5 × 야간근로시간 (야간근로는 기본급에 50% 가산)
        BigDecimal nightShiftAllowance = calculateAllowance(hourlyRate, NIGHT_SHIFT_RATE, nightShiftHours);
        details.put(EarningType.NIGHT_SHIFT_ALLOWANCE,
                nightShiftHours > 0
                        ? EarningAmount.of(EarningType.NIGHT_SHIFT_ALLOWANCE, nightShiftAllowance)
                        : EarningAmount.notApplicable(EarningType.NIGHT_SHIFT_ALLOWANCE));

        // 휴일근로수당 = 시급 × 1.5 × 휴일근로시간
        BigDecimal holidayAllowance = calculateAllowance(hourlyRate, HOLIDAY_RATE, holidayHours);
        details.put(EarningType.HOLIDAY_ALLOWANCE,
                holidayHours > 0
                        ? EarningAmount.of(EarningType.HOLIDAY_ALLOWANCE, holidayAllowance)
                        : EarningAmount.notApplicable(EarningType.HOLIDAY_ALLOWANCE));

        // 주휴수당 = 시급 × 주휴시간
        BigDecimal weeklyHolidayAllowance = hourlyRate.multiply(BigDecimal.valueOf(weeklyHolidayHours))
                .setScale(0, RoundingMode.FLOOR);
        details.put(EarningType.WEEKLY_HOLIDAY_ALLOWANCE,
                weeklyHolidayHours > 0
                        ? EarningAmount.of(EarningType.WEEKLY_HOLIDAY_ALLOWANCE, weeklyHolidayAllowance)
                        : EarningAmount.notApplicable(EarningType.WEEKLY_HOLIDAY_ALLOWANCE));

        // 추가 지급항목 (식대, 차량유지비, 직책수당 등)
        if (additionalEarnings != null) {
            for (Map.Entry<EarningType, BigDecimal> entry : additionalEarnings.entrySet()) {
                EarningType type = entry.getKey();
                BigDecimal amount = entry.getValue();
                if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                    details.put(type, EarningAmount.of(type, amount));
                } else {
                    details.putIfAbsent(type, EarningAmount.notApplicable(type));
                }
            }
        }

        // 나머지 항목은 미적용 처리
        for (EarningType type : EarningType.values()) {
            details.putIfAbsent(type, EarningAmount.notApplicable(type));
        }

        return EarningCalculationResult.of(details);
    }

    /**
     * 수당 계산 (시급 × 가산율 × 시간)
     */
    private BigDecimal calculateAllowance(BigDecimal hourlyRate, BigDecimal rate, double hours) {
        if (hours <= 0) {
            return BigDecimal.ZERO;
        }
        return hourlyRate
                .multiply(rate)
                .multiply(BigDecimal.valueOf(hours))
                .setScale(0, RoundingMode.FLOOR);
    }

    /**
     * 주휴시간 계산
     * - 주 15시간 이상 근무 시 발생
     * - 주휴시간 = (주 소정근로시간 / 5) × 1일
     * 
     * @param weeklyWorkHours 주 소정근로시간
     * @return 주휴시간 (주 15시간 미만이면 0)
     */
    public double calculateWeeklyHolidayHours(double weeklyWorkHours) {
        if (weeklyWorkHours < 15) {
            return 0;
        }
        // 주 5일 기준, 1일 근무시간 = 주 근무시간 / 5
        return weeklyWorkHours / 5;
    }
}
