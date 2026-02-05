package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.PayrollFormulaVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaPayrollFormulaVersionRepository extends JpaRepository<PayrollFormulaVersion, Long> {
    
    @Query("SELECT v FROM PayrollFormulaVersion v WHERE v.storeId = :storeId ORDER BY v.versionNumber DESC LIMIT 1")
    Optional<PayrollFormulaVersion> findLatestByStoreId(@Param("storeId") Long storeId);

    Optional<PayrollFormulaVersion> findByStoreIdAndVersionNumber(Long storeId, Integer versionNumber);

    @Query("SELECT v FROM PayrollFormulaVersion v WHERE v.storeId = :storeId ORDER BY v.versionNumber DESC")
    List<PayrollFormulaVersion> findAllByStoreIdOrderByVersionNumberDesc(@Param("storeId") Long storeId);
}
