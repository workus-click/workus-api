package com.workus.workus.payroll.deduction.infra.repository;

import com.workus.workus.payroll.deduction.domain.model.IncomeTaxTable;
import com.workus.workus.payroll.deduction.domain.repository.IncomeTaxTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IncomeTaxTableRepositoryImpl implements IncomeTaxTableRepository {

    private final JpaIncomeTaxTableRepository jpaRepository;

    @Override
    public Optional<IncomeTaxTable> findByYearAndSalaryAndDependents(String year, int salary, int dependentsCnt) {
        return jpaRepository.findByYearAndSalaryAndDependents(year, salary, dependentsCnt);
    }

    @Override
    public List<IncomeTaxTable> findAllByYearAndDependents(String year, int dependentsCnt) {
        return jpaRepository.findAllByYearAndTotalDependentsCntOrderBySalaryFrom(year, dependentsCnt);
    }

    @Override
    public List<IncomeTaxTable> findAllByYear(String year) {
        return jpaRepository.findAllByYearOrderByTotalDependentsCntAscSalaryFromAsc(year);
    }
}
