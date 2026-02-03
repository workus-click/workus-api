package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.repository.SalaryCalculationFormulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SalaryCalculationFormulaRepositoryImpl implements SalaryCalculationFormulaRepository {
    private final JpaSalaryCalculationFormulaRepository jpaSalaryCalculationFormulaRepository;

    @Override
    public Optional<SalaryCalculationFormula> findById(Long id) {
        return jpaSalaryCalculationFormulaRepository.findById(id);
    }

    @Override
    public List<SalaryCalculationFormula> findAllByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaSalaryCalculationFormulaRepository.findAllById(ids);
    }

    @Override
    public Optional<SalaryCalculationFormula> findByStoreIdAndFormulaType(Long storeId, FormulaType formulaType) {
        return jpaSalaryCalculationFormulaRepository.findByStoreIdAndFormulaType(storeId, formulaType);
    }

    @Override
    public SalaryCalculationFormula save(SalaryCalculationFormula formula) {
        return jpaSalaryCalculationFormulaRepository.save(formula);
    }
    // delete 메서드 제거 - 계산식은 immutable하므로 삭제 불가
}
