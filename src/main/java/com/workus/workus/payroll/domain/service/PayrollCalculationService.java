package com.workus.workus.payroll.domain.service;

import com.workus.workus.payroll.deduction.domain.model.DeductionCalculationResult;
import com.workus.workus.payroll.deduction.domain.service.DeductionCalculationService;
import com.workus.workus.payroll.domain.model.PayrollCalculationDto;
import com.workus.workus.payroll.earning.domain.model.EarningCalculationResult;
import com.workus.workus.payroll.earning.domain.model.EarningType;
import com.workus.workus.payroll.earning.domain.service.EarningCalculationService;
import com.workus.workus.payroll.employee.domain.model.Nationality;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 급여 계산 통합 서비스
 * - 지급항목 계산 → 공제항목 계산 → 차인지급액 산출
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollCalculationService {

    private final EarningCalculationService earningCalculationService;
    private final DeductionCalculationService deductionCalculationService;

    /**
     * 급여 계산
     *
     * @param storeUserId 매장 사용자 ID
     * @param baseSalary 기본급
     * @param hourlyRate 시급 (수당 계산용)
     * @param overtimeHours 연장근로시간
     * @param nightShiftHours 야간근로시간
     * @param holidayHours 휴일근로시간
     * @param weeklyHolidayHours 주휴시간
     * @param additionalEarnings 추가 지급항목
     * @param age 만 나이
     * @param contractMonths 계약 기간 (개월)
     * @param weeklyWorkHours 주당 근무시간
     * @param dependentsCnt 부양가족 수
     * @param nationality 국적
     * @param year 적용연도
     * @return 급여 계산 결과
     */
    public PayrollCalculationDto calculate(
            Long storeUserId,
            BigDecimal baseSalary,
            BigDecimal hourlyRate,
            double overtimeHours,
            double nightShiftHours,
            double holidayHours,
            double weeklyHolidayHours,
            Map<EarningType, BigDecimal> additionalEarnings,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            int dependentsCnt,
            Nationality nationality,
            String year) {

        // 1. 지급항목 계산
        EarningCalculationResult earnings = earningCalculationService.calculate(
                baseSalary,
                hourlyRate,
                overtimeHours,
                nightShiftHours,
                holidayHours,
                weeklyHolidayHours,
                additionalEarnings
        );

        // 2. 공제항목 계산 (과세 대상 급여 기준)
        DeductionCalculationResult deductions = deductionCalculationService.calculate(
                storeUserId,
                earnings.totalAmount(),      // 총 지급액 (4대보험 기준)
                earnings.taxableAmount(),    // 과세 대상 급여 (소득세 기준)
                age,
                contractMonths,
                weeklyWorkHours,
                dependentsCnt,
                nationality,
                year
        );

        // 3. 최종 결과 (차인지급액 포함)
        return PayrollCalculationDto.of(earnings, deductions);
    }

    /**
     * 시급제 근로자 급여 계산
     */
    public PayrollCalculationDto calculateForHourlyWorker(
            Long storeUserId,
            BigDecimal hourlyRate,
            int workHoursPerDay,
            int workDaysPerWeek,
            double overtimeHours,
            double nightShiftHours,
            double holidayHours,
            Map<EarningType, BigDecimal> additionalEarnings,
            int age,
            int contractMonths,
            int dependentsCnt,
            Nationality nationality,
            String year) {

        // 월 기본급 = 시급 × 일 근무시간 × 주 근무일 × 4주
        int monthlyWorkHours = workHoursPerDay * workDaysPerWeek * 4;
        BigDecimal baseSalary = hourlyRate.multiply(BigDecimal.valueOf(monthlyWorkHours));

        // 주 근무시간
        double weeklyWorkHours = workHoursPerDay * workDaysPerWeek;

        // 주휴시간 계산
        double weeklyHolidayHours = earningCalculationService.calculateWeeklyHolidayHours(weeklyWorkHours);
        // 월 주휴시간 = 주휴시간 × 4주
        double monthlyWeeklyHolidayHours = weeklyHolidayHours * 4;

        return calculate(
                storeUserId,
                baseSalary,
                hourlyRate,
                overtimeHours,
                nightShiftHours,
                holidayHours,
                monthlyWeeklyHolidayHours,
                additionalEarnings,
                age,
                contractMonths,
                weeklyWorkHours,
                dependentsCnt,
                nationality,
                year
        );
    }

    /**
     * 월급제 근로자 급여 계산
     */
    public PayrollCalculationDto calculateForMonthlyWorker(
            Long storeUserId,
            BigDecimal monthlySalary,
            BigDecimal hourlyRate,
            double overtimeHours,
            double nightShiftHours,
            double holidayHours,
            Map<EarningType, BigDecimal> additionalEarnings,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            int dependentsCnt,
            Nationality nationality,
            String year) {

        // 월급제는 주휴수당이 기본급에 포함되어 있음
        return calculate(
                storeUserId,
                monthlySalary,
                hourlyRate,
                overtimeHours,
                nightShiftHours,
                holidayHours,
                0,  // 주휴수당 별도 계산 안함
                additionalEarnings,
                age,
                contractMonths,
                weeklyWorkHours,
                dependentsCnt,
                nationality,
                year
        );
    }
}
