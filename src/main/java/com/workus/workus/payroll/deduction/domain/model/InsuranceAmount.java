package com.workus.workus.payroll.deduction.domain.model;

import java.math.BigDecimal;

/**
 * 보험별 부담금액
 */
public record InsuranceAmount(
        BigDecimal employeeAmount,  // 직원 부담액
        BigDecimal employerAmount,  // 회사 부담액
        boolean applicable          // 적용 여부
) {
    public static InsuranceAmount notApplicable() {
        return new InsuranceAmount(BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    public static InsuranceAmount of(BigDecimal employeeAmount, BigDecimal employerAmount) {
        return new InsuranceAmount(employeeAmount, employerAmount, true);
    }

    public BigDecimal totalAmount() {
        return employeeAmount.add(employerAmount);
    }
}
