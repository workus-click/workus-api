package com.workus.workus.payroll.deduction.infra.repository;

import com.workus.workus.payroll.deduction.domain.model.IncomeTaxTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaIncomeTaxTableRepository extends JpaRepository<IncomeTaxTable, Long> {

    @Query("SELECT t FROM IncomeTaxTable t " +
           "WHERE t.year = :year " +
           "AND t.salaryFrom <= :salary AND t.salaryTo >= :salary " +
           "AND t.totalDependentsCnt = :dependentsCnt")
    Optional<IncomeTaxTable> findByYearAndSalaryAndDependents(
            @Param("year") String year,
            @Param("salary") int salary,
            @Param("dependentsCnt") int dependentsCnt);

    List<IncomeTaxTable> findAllByYearAndTotalDependentsCntOrderBySalaryFrom(String year, int totalDependentsCnt);

    List<IncomeTaxTable> findAllByYearOrderByTotalDependentsCntAscSalaryFromAsc(String year);
}
