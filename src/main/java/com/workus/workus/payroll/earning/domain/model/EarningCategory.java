package com.workus.workus.payroll.earning.domain.model;

/**
 * 지급항목 카테고리
 */
public enum EarningCategory {
    BASE_PAY("기본급여"),
    ALLOWANCE("수당"),
    BONUS("상여");

    private final String description;

    EarningCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
