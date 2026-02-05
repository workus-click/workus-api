package com.workus.workus.payroll.deduction.domain.model;

/**
 * 공제 항목 카테고리
 */
public enum DeductionCategory {
    SOCIAL_INSURANCE("사회보험"),
    TAX("세금");

    private final String description;

    DeductionCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
