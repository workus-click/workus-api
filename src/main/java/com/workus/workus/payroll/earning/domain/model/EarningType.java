package com.workus.workus.payroll.earning.domain.model;

import lombok.Getter;

/**
 * 지급항목 타입
 * - 기본급, 각종 수당 등 급여에 포함되는 항목
 * - 계산 순서(order)에 따라 순차 계산
 * 
 * 과세/비과세 구분:
 * - 과세: 기본급, 연장/야간/휴일 근로수당
 * - 비과세: 식대(월 20만원 한도), 차량유지비(월 20만원 한도) 등
 */
@Getter
public enum EarningType {
    // 기본급여
    BASE_SALARY(1, "기본급", EarningCategory.BASE_PAY, true),

    // 법정수당 (근로기준법 기반)
    OVERTIME_ALLOWANCE(2, "연장근로수당", EarningCategory.ALLOWANCE, true),
    NIGHT_SHIFT_ALLOWANCE(3, "야간근로수당", EarningCategory.ALLOWANCE, true),
    HOLIDAY_ALLOWANCE(4, "휴일근로수당", EarningCategory.ALLOWANCE, true),
    WEEKLY_HOLIDAY_ALLOWANCE(5, "주휴수당", EarningCategory.ALLOWANCE, true),

    // 비과세 수당
    MEAL_ALLOWANCE(6, "식대", EarningCategory.ALLOWANCE, false),
    VEHICLE_ALLOWANCE(7, "차량유지비", EarningCategory.ALLOWANCE, false),
    CHILDCARE_ALLOWANCE(8, "육아수당", EarningCategory.ALLOWANCE, false),

    // 기타 수당
    POSITION_ALLOWANCE(9, "직책수당", EarningCategory.ALLOWANCE, true),
    SKILL_ALLOWANCE(10, "기술수당", EarningCategory.ALLOWANCE, true),
    TENURE_ALLOWANCE(11, "근속수당", EarningCategory.ALLOWANCE, true),

    // 상여
    BONUS(12, "상여금", EarningCategory.BONUS, true);

    private final int order;
    private final String description;
    private final EarningCategory category;
    private final boolean taxable;  // 과세 여부

    EarningType(int order, String description, EarningCategory category, boolean taxable) {
        this.order = order;
        this.description = description;
        this.category = category;
        this.taxable = taxable;
    }

    /**
     * 기본급 여부
     */
    public boolean isBasePay() {
        return category == EarningCategory.BASE_PAY;
    }

    /**
     * 수당 여부
     */
    public boolean isAllowance() {
        return category == EarningCategory.ALLOWANCE;
    }

    /**
     * 상여 여부
     */
    public boolean isBonus() {
        return category == EarningCategory.BONUS;
    }

    /**
     * 비과세 여부
     */
    public boolean isNonTaxable() {
        return !taxable;
    }

    /**
     * 법정수당 여부 (연장/야간/휴일/주휴)
     */
    public boolean isLegalAllowance() {
        return this == OVERTIME_ALLOWANCE 
                || this == NIGHT_SHIFT_ALLOWANCE 
                || this == HOLIDAY_ALLOWANCE
                || this == WEEKLY_HOLIDAY_ALLOWANCE;
    }

    /**
     * 순서대로 정렬된 지급항목 목록
     */
    public static EarningType[] orderedValues() {
        EarningType[] values = values();
        java.util.Arrays.sort(values, java.util.Comparator.comparingInt(EarningType::getOrder));
        return values;
    }

    /**
     * 과세 항목만 필터링
     */
    public static EarningType[] taxableTypes() {
        return java.util.Arrays.stream(values())
                .filter(EarningType::isTaxable)
                .sorted(java.util.Comparator.comparingInt(EarningType::getOrder))
                .toArray(EarningType[]::new);
    }

    /**
     * 비과세 항목만 필터링
     */
    public static EarningType[] nonTaxableTypes() {
        return java.util.Arrays.stream(values())
                .filter(EarningType::isNonTaxable)
                .sorted(java.util.Comparator.comparingInt(EarningType::getOrder))
                .toArray(EarningType[]::new);
    }
}
