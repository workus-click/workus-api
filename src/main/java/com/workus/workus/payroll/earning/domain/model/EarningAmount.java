package com.workus.workus.payroll.earning.domain.model;

import java.math.BigDecimal;

/**
 * 지급항목별 금액
 */
public record EarningAmount(
        EarningType type,
        BigDecimal amount,
        boolean applicable,
        Long formulaId  // 사용된 계산식 ID
) {
    public static EarningAmount notApplicable(EarningType type) {
        return new EarningAmount(type, BigDecimal.ZERO, false, null);
    }

    public static EarningAmount of(EarningType type, BigDecimal amount) {
        return new EarningAmount(type, amount, true, null);
    }

    public static EarningAmount of(EarningType type, BigDecimal amount, Long formulaId) {
        return new EarningAmount(type, amount, true, formulaId);
    }

    /**
     * 과세 대상 금액
     */
    public BigDecimal taxableAmount() {
        if (!applicable || type.isNonTaxable()) {
            return BigDecimal.ZERO;
        }
        return amount;
    }

    /**
     * 비과세 금액
     */
    public BigDecimal nonTaxableAmount() {
        if (!applicable || type.isTaxable()) {
            return BigDecimal.ZERO;
        }
        return amount;
    }
}
