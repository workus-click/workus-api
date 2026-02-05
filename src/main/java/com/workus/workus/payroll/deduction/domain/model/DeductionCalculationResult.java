package com.workus.workus.payroll.deduction.domain.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 전체 공제 계산 결과
 */
public record DeductionCalculationResult(
        Map<DeductionType, DeductionAmount> details,
        BigDecimal totalEmployeeAmount,   // 직원 부담 총액
        BigDecimal totalEmployerAmount    // 회사 부담 총액
) {
    public static DeductionCalculationResult of(Map<DeductionType, DeductionAmount> details) {
        BigDecimal totalEmployee = details.values().stream()
                .filter(DeductionAmount::applicable)
                .map(DeductionAmount::employeeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEmployer = details.values().stream()
                .filter(DeductionAmount::applicable)
                .map(DeductionAmount::employerAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DeductionCalculationResult(details, totalEmployee, totalEmployer);
    }

    /**
     * 총 공제액 (직원 부담만)
     */
    public BigDecimal totalDeduction() {
        return totalEmployeeAmount;
    }

    /**
     * 특정 항목 적용 여부
     */
    public boolean isApplicable(DeductionType type) {
        DeductionAmount amount = details.get(type);
        return amount != null && amount.applicable();
    }

    /**
     * 특정 항목 직원 부담액
     */
    public BigDecimal getEmployeeAmount(DeductionType type) {
        DeductionAmount amount = details.get(type);
        return amount != null ? amount.employeeAmount() : BigDecimal.ZERO;
    }

    /**
     * 특정 항목 회사 부담액
     */
    public BigDecimal getEmployerAmount(DeductionType type) {
        DeductionAmount amount = details.get(type);
        return amount != null ? amount.employerAmount() : BigDecimal.ZERO;
    }

    /**
     * 사회보험 직원 부담 총액
     */
    public BigDecimal totalSocialInsuranceEmployee() {
        return details.entrySet().stream()
                .filter(e -> e.getKey().isSocialInsurance() && e.getValue().applicable())
                .map(e -> e.getValue().employeeAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 세금 총액
     */
    public BigDecimal totalTax() {
        return details.entrySet().stream()
                .filter(e -> e.getKey().isTax() && e.getValue().applicable())
                .map(e -> e.getValue().employeeAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
