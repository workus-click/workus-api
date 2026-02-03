package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaSalaryCalculationFormulaRepository extends JpaRepository<SalaryCalculationFormula, Long> {
    @Query("SELECT s FROM SalaryCalculationFormula s WHERE s.storeId = :storeId AND s.formula.formulaType = :formulaType")
    Optional<SalaryCalculationFormula> findByStoreIdAndFormulaType(@Param("storeId") Long storeId, @Param("formulaType") FormulaType formulaType);
}
