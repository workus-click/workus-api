package com.workus.workus.payroll.formula.domain.repository;

import com.workus.workus.payroll.formula.domain.model.PayrollFormulaVersion;

import java.util.List;
import java.util.Optional;

public interface PayrollFormulaVersionRepository {
    /**
     * 버전 ID로 조회
     */
    Optional<PayrollFormulaVersion> findById(Long id);

    /**
     * 매장의 최신 버전 조회
     */
    Optional<PayrollFormulaVersion> findLatestByStoreId(Long storeId);

    /**
     * 매장의 특정 버전 조회
     */
    Optional<PayrollFormulaVersion> findByStoreIdAndVersionNumber(Long storeId, Integer versionNumber);

    /**
     * 매장의 모든 버전 조회 (버전 번호 내림차순)
     */
    List<PayrollFormulaVersion> findAllByStoreId(Long storeId);

    /**
     * 버전 저장
     */
    PayrollFormulaVersion save(PayrollFormulaVersion version);
}
