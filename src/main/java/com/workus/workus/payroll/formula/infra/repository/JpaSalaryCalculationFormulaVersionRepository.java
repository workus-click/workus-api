package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormulaVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaSalaryCalculationFormulaVersionRepository extends JpaRepository<SalaryCalculationFormulaVersion, Long> {
    
    @Query("SELECT v FROM SalaryCalculationFormulaVersion v WHERE v.storeId = :storeId ORDER BY v.versionNumber DESC LIMIT 1")
    Optional<SalaryCalculationFormulaVersion> findLatestByStoreId(@Param("storeId") Long storeId);

    Optional<SalaryCalculationFormulaVersion> findByStoreIdAndVersionNumber(Long storeId, Integer versionNumber);

    @Query("SELECT v FROM SalaryCalculationFormulaVersion v WHERE v.storeId = :storeId ORDER BY v.versionNumber DESC")
    List<SalaryCalculationFormulaVersion> findAllByStoreIdOrderByVersionNumberDesc(@Param("storeId") Long storeId);
}
