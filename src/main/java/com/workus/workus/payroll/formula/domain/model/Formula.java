package com.workus.workus.payroll.formula.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

// 계산식 VO
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Formula {
    @Convert(converter = FormulaTypeConverter.class)
    @Column(name = "formula_type")
    private FormulaType formulaType;

    @Column(name = "formula")
    private String expression;

    public Formula(FormulaType formulaType, String expression) {
        this.formulaType = formulaType;
        this.expression = expression;
    }

    public static Formula of(FormulaType formulaType, String expression) {
        return new Formula(formulaType, expression);
    }

    public BigDecimal calculate(Set<String> allowedVariables, Map<String, BigDecimal> values) {
        validateFormula(expression, allowedVariables, values);

        Expression exp = new ExpressionBuilder(expression)
                .variables(allowedVariables)
                .build();

        for (String variable : allowedVariables) {
            exp.setVariable(
                    variable,
                    values.get(variable).doubleValue()
            );
        }

        return BigDecimal.valueOf(exp.evaluate()).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateFormula(String expression, Set<String> variables, Map<String, BigDecimal> values) {
        validateAllowedVariables(variables, values);
        validateSyntax(expression, variables);
    }

    private void validateAllowedVariables(Set<String> variables, Map<String, BigDecimal> values) {
        for (String variable : variables) {
            if (!values.containsKey(variable)) {
                throw new IllegalArgumentException(
                        "누락된 변수: " + variable
                );
            }
        }
    }

    private void validateSyntax(String expression, Set<String> variables) {
        try {
            new ExpressionBuilder(expression)
                    .variables(variables)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 계산식 문법", e);
        }
    }
}
