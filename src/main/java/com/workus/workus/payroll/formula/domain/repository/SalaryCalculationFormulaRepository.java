package com.workus.workus.payroll.formula.domain.repository;

import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;

import java.util.List;
import java.util.Optional;

/**
 * 급여 계산식 저장소 (Immutable - INSERT만 지원)
 */
public interface SalaryCalculationFormulaRepository {
    Optional<SalaryCalculationFormula> findById(Long id);
    List<SalaryCalculationFormula> findAllByIdIn(List<Long> ids);
    Optional<SalaryCalculationFormula> findByStoreIdAndFormulaType(Long storeId, FormulaType formulaType);
    SalaryCalculationFormula save(SalaryCalculationFormula formula);
    // delete 메서드 제거 - 계산식은 immutable하므로 삭제 불가
}
