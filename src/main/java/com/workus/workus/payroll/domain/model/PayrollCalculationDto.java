package com.workus.workus.payroll.domain.model;

import com.workus.workus.payroll.deduction.domain.model.DeductionCalculationResult;
import com.workus.workus.payroll.earning.domain.model.EarningCalculationResult;

import java.math.BigDecimal;

/**
 * 급여 계산 결과 DTO (저장 전 계산용)
 */
public record PayrollCalculationDto(
        EarningCalculationResult earnings,      // 지급항목 상세
        DeductionCalculationResult deductions,  // 공제항목 상세
        BigDecimal totalEarning,                // 총 지급액
        BigDecimal totalDeduction,              // 총 공제액
        BigDecimal netPay,                      // 차인지급액 (실수령액)
        Long formulaVersionId                   // 사용된 계산식 버전 ID
) {
    public static PayrollCalculationDto of(
            EarningCalculationResult earnings,
            DeductionCalculationResult deductions) {
        return of(earnings, deductions, null);
    }

    public static PayrollCalculationDto of(
            EarningCalculationResult earnings,
            DeductionCalculationResult deductions,
            Long formulaVersionId) {

        BigDecimal totalEarning = earnings.totalAmount();
        BigDecimal totalDeduction = deductions.totalEmployeeAmount();
        BigDecimal netPay = totalEarning.subtract(totalDeduction);

        return new PayrollCalculationDto(
                earnings,
                deductions,
                totalEarning,
                totalDeduction,
                netPay,
                formulaVersionId
        );
    }

    /**
     * 과세 대상 급여
     */
    public BigDecimal taxableEarning() {
        return earnings.taxableAmount();
    }

    /**
     * 비과세 급여
     */
    public BigDecimal nonTaxableEarning() {
        return earnings.nonTaxableAmount();
    }

    /**
     * 4대보험 공제 총액
     */
    public BigDecimal totalSocialInsurance() {
        return deductions.totalSocialInsuranceEmployee();
    }

    /**
     * 세금 공제 총액
     */
    public BigDecimal totalTax() {
        return deductions.totalTax();
    }

    /**
     * 회사 부담 총액
     */
    public BigDecimal totalEmployerContribution() {
        return deductions.totalEmployerAmount();
    }
}
