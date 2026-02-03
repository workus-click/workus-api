package com.workus.workus.payroll.formula.domain.repository;

import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormulaVersion;

import java.util.List;
import java.util.Optional;

public interface SalaryCalculationFormulaVersionRepository {
    /**
     * 버전 ID로 조회
     */
    Optional<SalaryCalculationFormulaVersion> findById(Long id);

    /**
     * 매장의 최신 버전 조회
     */
    Optional<SalaryCalculationFormulaVersion> findLatestByStoreId(Long storeId);

    /**
     * 매장의 특정 버전 조회
     */
    Optional<SalaryCalculationFormulaVersion> findByStoreIdAndVersionNumber(Long storeId, Integer versionNumber);

    /**
     * 매장의 모든 버전 조회 (버전 번호 내림차순)
     */
    List<SalaryCalculationFormulaVersion> findAllByStoreId(Long storeId);

    /**
     * 버전 저장
     */
    SalaryCalculationFormulaVersion save(SalaryCalculationFormulaVersion version);
}
