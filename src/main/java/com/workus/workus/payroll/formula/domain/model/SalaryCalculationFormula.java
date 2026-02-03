package com.workus.workus.payroll.formula.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import com.workus.workus.payroll.formula.domain.model.Formula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 급여 계산식 (Immutable)
 * - INSERT만 가능, UPDATE/DELETE 불가
 * - 계산식 변경이 필요한 경우 새로운 계산식을 생성하고 버전을 올림
 */
@Entity
@Table(name = "salary_calculation_formula")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SalaryCalculationFormula extends BaseEntity {
    @Id
    @Column(name = "formula_id")
    private Long id;

    @Column(name = "store_id", updatable = false)
    private Long storeId;

    @Embedded
    private Formula formula;

    /**
     * 계산식이 유효한지 검증합니다.
     * variableValues의 키들이 허용된 변수 목록으로 사용됩니다.
     * 
     * @param variableValues 변수 값들 (변수명, 값) - 키가 허용된 변수 목록
     * @throws IllegalArgumentException 계산식이 유효하지 않거나 변수가 누락된 경우
     */
    public void validateFormula(Map<String, BigDecimal> variableValues) {
        if (formula == null) {
            throw new IllegalArgumentException("계산식이 설정되지 않았습니다.");
        }
        Set<String> allowedVariables = variableValues.keySet();
        formula.calculate(allowedVariables, variableValues);
    }

    /**
     * 계산식을 평가하여 결과를 반환합니다.
     * variableValues의 키들이 허용된 변수 목록으로 사용됩니다.
     * 
     * @param variableValues 변수 값들 (변수명, 값) - 키가 허용된 변수 목록
     * @return 계산 결과 (BigDecimal)
     * @throws IllegalArgumentException 계산식이 유효하지 않거나 변수가 누락된 경우
     */
    public BigDecimal calculate(Map<String, BigDecimal> variableValues) {
        if (formula == null) {
            throw new IllegalArgumentException("계산식이 설정되지 않았습니다.");
        }
        Set<String> allowedVariables = variableValues.keySet();
        return formula.calculate(allowedVariables, variableValues);
    }

    /**
     * 계산식 타입 반환
     */
    public FormulaType getFormulaType() {
        return formula != null ? formula.getFormulaType() : null;
    }

    /**
     * 새로운 계산식 생성 (Immutable - 생성만 가능)
     */
    public static SalaryCalculationFormula of(Long storeId, Formula formula) {
        return SalaryCalculationFormula.builder()
                .id(IdGenerator.nextId())
                .storeId(storeId)
                .formula(formula)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryCalculationFormula that = (SalaryCalculationFormula) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

