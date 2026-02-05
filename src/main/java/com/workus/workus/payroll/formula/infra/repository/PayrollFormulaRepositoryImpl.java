package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.PayrollFormula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.repository.PayrollFormulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PayrollFormulaRepositoryImpl implements PayrollFormulaRepository {
    private final JpaPayrollFormulaRepository jpaPayrollFormulaRepository;

    @Override
    public Optional<PayrollFormula> findById(Long id) {
        return jpaPayrollFormulaRepository.findById(id);
    }

    @Override
    public List<PayrollFormula> findAllByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaPayrollFormulaRepository.findAllById(ids);
    }

    @Override
    public Optional<PayrollFormula> findByStoreIdAndFormulaType(Long storeId, FormulaType formulaType) {
        return jpaPayrollFormulaRepository.findByStoreIdAndFormulaType(storeId, formulaType);
    }

    @Override
    public PayrollFormula save(PayrollFormula formula) {
        return jpaPayrollFormulaRepository.save(formula);
    }
    // delete 메서드 제거 - 계산식은 immutable하므로 삭제 불가
}
