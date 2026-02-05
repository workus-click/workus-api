package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.PayrollFormulaVersion;
import com.workus.workus.payroll.formula.domain.repository.PayrollFormulaVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PayrollFormulaVersionRepositoryImpl implements PayrollFormulaVersionRepository {
    private final JpaPayrollFormulaVersionRepository jpaRepository;

    @Override
    public Optional<PayrollFormulaVersion> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<PayrollFormulaVersion> findLatestByStoreId(Long storeId) {
        return jpaRepository.findLatestByStoreId(storeId);
    }

    @Override
    public Optional<PayrollFormulaVersion> findByStoreIdAndVersionNumber(Long storeId, Integer versionNumber) {
        return jpaRepository.findByStoreIdAndVersionNumber(storeId, versionNumber);
    }

    @Override
    public List<PayrollFormulaVersion> findAllByStoreId(Long storeId) {
        return jpaRepository.findAllByStoreIdOrderByVersionNumberDesc(storeId);
    }

    @Override
    public PayrollFormulaVersion save(PayrollFormulaVersion version) {
        return jpaRepository.save(version);
    }
}
