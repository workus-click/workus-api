package com.workus.workus.payroll.formula.presentation.dto;

import com.workus.workus.payroll.formula.domain.model.FormulaCategory;
import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormula;

public record FormulaDetailResponse(
        Long formulaId,
        String formulaType,
        String category,
        String expression
) {
    public static FormulaDetailResponse from(SalaryCalculationFormula formula) {
        return new FormulaDetailResponse(
                formula.getId(),
                ((Enum<?>) formula.getFormulaType()).name(),
                formula.getFormulaType().getFormulaCategory().name(),
                formula.getFormula().getExpression()
        );
    }
}
