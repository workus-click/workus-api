package com.workus.workus.payroll.formula.infra.repository;

import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormulaVersion;
import com.workus.workus.payroll.formula.domain.repository.SalaryCalculationFormulaVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SalaryCalculationFormulaVersionRepositoryImpl implements SalaryCalculationFormulaVersionRepository {
    private final JpaSalaryCalculationFormulaVersionRepository jpaRepository;

    @Override
    public Optional<SalaryCalculationFormulaVersion> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<SalaryCalculationFormulaVersion> findLatestByStoreId(Long storeId) {
        return jpaRepository.findLatestByStoreId(storeId);
    }

    @Override
    public Optional<SalaryCalculationFormulaVersion> findByStoreIdAndVersionNumber(Long storeId, Integer versionNumber) {
        return jpaRepository.findByStoreIdAndVersionNumber(storeId, versionNumber);
    }

    @Override
    public List<SalaryCalculationFormulaVersion> findAllByStoreId(Long storeId) {
        return jpaRepository.findAllByStoreIdOrderByVersionNumberDesc(storeId);
    }

    @Override
    public SalaryCalculationFormulaVersion save(SalaryCalculationFormulaVersion version) {
        return jpaRepository.save(version);
    }
}
