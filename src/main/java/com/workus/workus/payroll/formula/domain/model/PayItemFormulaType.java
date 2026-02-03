package com.workus.workus.payroll.formula.domain.model;
import lombok.Getter;

@Getter
public enum PayItemFormulaType implements FormulaType {
    BASE_SALARY(FormulaCategory.PAY_ITEM),            // 기본급
    OVERTIME_ALLOWANCE(FormulaCategory.PAY_ITEM),     // 초과근무수당(연장근무수당 -> 초과근무수당)
    NIGHT_SHIFT_ALLOWANCE(FormulaCategory.PAY_ITEM),  // 야간근무수당
    HOLIDAY_ALLOWANCE(FormulaCategory.PAY_ITEM),      // 휴일근무수당
    OTHER_EARNINGS(FormulaCategory.PAY_ITEM),         // 기타지급항목(현재 미사용)
    BONUS_PAY(FormulaCategory.PAY_ITEM),              // 상여금(현재 미사용)
    GENERAL_ALLOWANCE(FormulaCategory.PAY_ITEM),      // 일반수당(현재 미사용)
    WEEKLY_HOLIDAY_ALLOWANCE(FormulaCategory.PAY_ITEM); // 주휴수당

    private final FormulaCategory formulaCategory;

    PayItemFormulaType(FormulaCategory formulaCategory) {
        this.formulaCategory = formulaCategory;
    }
}
