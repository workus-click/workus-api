package com.workus.workus.payroll.deduction.domain.model;

import java.math.BigDecimal;

/**
 * 소득세 계산 결과
 */
public record IncomeTaxCalculationResult(
        BigDecimal incomeTax,       // 소득세
        BigDecimal localIncomeTax,  // 지방소득세 (소득세 × 10%)
        BigDecimal totalTax         // 총 세금
) {
    public static IncomeTaxCalculationResult of(BigDecimal incomeTax, BigDecimal localIncomeTax) {
        return new IncomeTaxCalculationResult(
                incomeTax,
                localIncomeTax,
                incomeTax.add(localIncomeTax)
        );
    }

    public static IncomeTaxCalculationResult zero() {
        return new IncomeTaxCalculationResult(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
