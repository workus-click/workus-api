package com.workus.workus.payroll.deduction.domain.model;

import java.math.BigDecimal;

/**
 * 공제 항목별 금액
 */
public record DeductionAmount(
        DeductionType type,
        BigDecimal employeeAmount,  // 직원 부담액
        BigDecimal employerAmount,  // 회사 부담액 (세금은 0)
        boolean applicable,         // 적용 여부
        Long formulaId              // 사용된 계산식 ID
) {
    public static DeductionAmount notApplicable(DeductionType type) {
        return new DeductionAmount(type, BigDecimal.ZERO, BigDecimal.ZERO, false, null);
    }

    public static DeductionAmount of(DeductionType type, BigDecimal employeeAmount, BigDecimal employerAmount) {
        return new DeductionAmount(type, employeeAmount, employerAmount, true, null);
    }

    public static DeductionAmount of(DeductionType type, BigDecimal employeeAmount, BigDecimal employerAmount, Long formulaId) {
        return new DeductionAmount(type, employeeAmount, employerAmount, true, formulaId);
    }

    /**
     * 세금용 (회사 부담 없음)
     */
    public static DeductionAmount taxOf(DeductionType type, BigDecimal amount) {
        return new DeductionAmount(type, amount, BigDecimal.ZERO, true, null);
    }

    public static DeductionAmount taxOf(DeductionType type, BigDecimal amount, Long formulaId) {
        return new DeductionAmount(type, amount, BigDecimal.ZERO, true, formulaId);
    }

    public BigDecimal totalAmount() {
        return employeeAmount.add(employerAmount);
    }
}
