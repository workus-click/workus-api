package com.workus.workus.payroll.formula.domain.repository;

import com.workus.workus.payroll.formula.domain.model.PayrollFormula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;

import java.util.List;
import java.util.Optional;

/**
 * 급여 계산식 저장소 (Immutable - INSERT만 지원)
 */
public interface PayrollFormulaRepository {
    Optional<PayrollFormula> findById(Long id);
    List<PayrollFormula> findAllByIdIn(List<Long> ids);
    Optional<PayrollFormula> findByStoreIdAndFormulaType(Long storeId, FormulaType formulaType);
    PayrollFormula save(PayrollFormula formula);
    // delete 메서드 제거 - 계산식은 immutable하므로 삭제 불가
}
