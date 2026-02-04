package com.workus.workus.payroll.tax.domain.service;

import com.workus.workus.payroll.tax.domain.model.IncomeTaxTable;
import com.workus.workus.payroll.tax.domain.repository.IncomeTaxTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeTaxTableService {

    private final IncomeTaxTableRepository incomeTaxTableRepository;

    /**
     * 연도, 급여, 부양가족수로 세액 단일 조회
     *
     * @param year 기준연도
     * @param salary 급여
     * @param dependentsCnt 부양가족수
     * @return 해당 세액 (없으면 Optional.empty)
     */
    public Optional<BigDecimal> getTaxAmount(String year, int salary, int dependentsCnt) {
        return incomeTaxTableRepository
                .findByYearAndSalaryAndDependents(year, salary, dependentsCnt)
                .map(IncomeTaxTable::getTaxAmount);
    }

    /**
     * 연도, 급여, 부양가족수로 간이세액표 엔티티 조회
     */
    private Optional<IncomeTaxTable> getIncomeTaxTable(String year, int salary, int dependentsCnt) {
        return incomeTaxTableRepository.findByYearAndSalaryAndDependents(year, salary, dependentsCnt);
    }

    /**
     * 연도별 전체 세액표를 Map으로 조회
     * Key: 부양가족수 -> 급여시작구간
     * Value: 세액
     *
     * @param year 기준연도
     * @return Map<부양가족수, Map<급여시작구간, 세액>>
     */
    public Map<Integer, Map<Integer, BigDecimal>> getTaxTableMapByYear(String year) {
        return incomeTaxTableRepository.findAllByYear(year)
                .stream()
                .collect(Collectors.groupingBy(
                        IncomeTaxTable::getTotalDependentsCnt,
                        Collectors.toMap(
                                IncomeTaxTable::getSalaryFrom,
                                IncomeTaxTable::getTaxAmount
                        )
                ));
    }
}
