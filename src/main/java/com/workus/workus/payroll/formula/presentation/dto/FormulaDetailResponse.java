package com.workus.workus.payroll.formula.presentation.dto;

import com.workus.workus.payroll.formula.domain.model.PayrollFormula;

public record FormulaDetailResponse(
        Long formulaId,
        String formulaType,
        String category,
        String expression
) {
    public static FormulaDetailResponse from(PayrollFormula formula) {
        return new FormulaDetailResponse(
                formula.getId(),
                ((Enum<?>) formula.getFormulaType()).name(),
                formula.getFormulaType().getFormulaCategory().name(),
                formula.getFormula().getExpression()
        );
    }
}
