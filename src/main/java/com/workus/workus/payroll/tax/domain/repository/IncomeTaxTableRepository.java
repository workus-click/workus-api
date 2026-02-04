package com.workus.workus.payroll.tax.domain.repository;

import com.workus.workus.payroll.tax.domain.model.IncomeTaxTable;

import java.util.List;
import java.util.Optional;

public interface IncomeTaxTableRepository {

    /**
     * 연도, 급여, 부양가족수에 해당하는 세액 조회
     */
    Optional<IncomeTaxTable> findByYearAndSalaryAndDependents(String year, int salary, int dependentsCnt);

    /**
     * 연도, 부양가족수에 해당하는 모든 급여 구간 조회
     */
    List<IncomeTaxTable> findAllByYearAndDependents(String year, int dependentsCnt);

    /**
     * 연도에 해당하는 모든 세액표 조회
     */
    List<IncomeTaxTable> findAllByYear(String year);
}
