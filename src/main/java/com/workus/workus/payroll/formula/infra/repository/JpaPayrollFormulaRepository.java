package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.PayrollFormula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaPayrollFormulaRepository extends JpaRepository<PayrollFormula, Long> {
    @Query("SELECT s FROM PayrollFormula s WHERE s.storeId = :storeId AND s.formula.formulaType = :formulaType")
    Optional<PayrollFormula> findByStoreIdAndFormulaType(@Param("storeId") Long storeId, @Param("formulaType") FormulaType formulaType);
}
