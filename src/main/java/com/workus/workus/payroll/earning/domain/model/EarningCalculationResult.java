package com.workus.workus.payroll.earning.domain.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 전체 지급항목 계산 결과
 */
public record EarningCalculationResult(
        Map<EarningType, EarningAmount> details,
        BigDecimal totalAmount,        // 총 지급액
        BigDecimal taxableAmount,      // 과세 대상 금액
        BigDecimal nonTaxableAmount    // 비과세 금액
) {
    public static EarningCalculationResult of(Map<EarningType, EarningAmount> details) {
        BigDecimal total = details.values().stream()
                .filter(EarningAmount::applicable)
                .map(EarningAmount::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxable = details.values().stream()
                .filter(EarningAmount::applicable)
                .map(EarningAmount::taxableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal nonTaxable = details.values().stream()
                .filter(EarningAmount::applicable)
                .map(EarningAmount::nonTaxableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new EarningCalculationResult(details, total, taxable, nonTaxable);
    }

    /**
     * 특정 항목 적용 여부
     */
    public boolean isApplicable(EarningType type) {
        EarningAmount amount = details.get(type);
        return amount != null && amount.applicable();
    }

    /**
     * 특정 항목 금액
     */
    public BigDecimal getAmount(EarningType type) {
        EarningAmount amount = details.get(type);
        return amount != null ? amount.amount() : BigDecimal.ZERO;
    }

    /**
     * 기본급 총액
     */
    public BigDecimal totalBasePay() {
        return details.entrySet().stream()
                .filter(e -> e.getKey().isBasePay() && e.getValue().applicable())
                .map(e -> e.getValue().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 수당 총액
     */
    public BigDecimal totalAllowance() {
        return details.entrySet().stream()
                .filter(e -> e.getKey().isAllowance() && e.getValue().applicable())
                .map(e -> e.getValue().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 상여 총액
     */
    public BigDecimal totalBonus() {
        return details.entrySet().stream()
                .filter(e -> e.getKey().isBonus() && e.getValue().applicable())
                .map(e -> e.getValue().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 법정수당 총액 (연장/야간/휴일/주휴)
     */
    public BigDecimal totalLegalAllowance() {
        return details.entrySet().stream()
                .filter(e -> e.getKey().isLegalAllowance() && e.getValue().applicable())
                .map(e -> e.getValue().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
