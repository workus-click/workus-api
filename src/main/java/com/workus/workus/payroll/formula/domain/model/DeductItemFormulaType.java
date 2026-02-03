package com.workus.workus.payroll.formula.domain.model;

import lombok.Getter;

@Getter
public enum DeductItemFormulaType implements FormulaType {
    INCOME_TAX(FormulaCategory.DEDUCT_ITEM),            // 소득세
    LOCAL_INCOME_TAX(FormulaCategory.DEDUCT_ITEM),       //지방소득세
    RESIDENT_TAX(FormulaCategory.DEDUCT_ITEM),         // 주민세
    NATIONAL_PENSION(FormulaCategory.DEDUCT_ITEM),     // 국민연금
    HEALTH_INSURANCE(FormulaCategory.DEDUCT_ITEM),     // 건강보험
    LONG_TERM_CARE_INSURANCE(FormulaCategory.DEDUCT_ITEM), // 장기요양보험
    EMPLOYMENT_INSURANCE(FormulaCategory.DEDUCT_ITEM), // 고용보험
    OTHER_DEDUCT_ITEMS(FormulaCategory.DEDUCT_ITEM);      // 기타공제항목

    private final FormulaCategory formulaCategory;

    DeductItemFormulaType(FormulaCategory formulaCategory) {
        this.formulaCategory = formulaCategory;
    }

}
