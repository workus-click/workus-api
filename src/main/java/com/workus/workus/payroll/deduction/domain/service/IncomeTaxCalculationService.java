package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.deduction.domain.model.IncomeTaxCalculationResult;
import com.workus.workus.payroll.deduction.domain.model.IncomeTaxTable;
import com.workus.workus.payroll.deduction.domain.repository.IncomeTaxTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 소득세 계산 서비스
 * 
 * - 소득세: 간이세액조견표에서 월소득액(비과세 제외) 구간에 해당하는 세액 조회
 * - 지방소득세: 소득세 × 10%
 * - 10원 미만 절사 처리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeTaxCalculationService {

    private static final BigDecimal LOCAL_TAX_RATE = new BigDecimal("0.1");  // 지방소득세 10%
    private static final int TRUNCATE_UNIT = 10;  // 10원 미만 절사

    private final IncomeTaxTableRepository incomeTaxTableRepository;

    /**
     * 소득세 및 지방소득세 계산
     *
     * @param taxableSalary 과세 대상 월급여 (비과세 제외, 천원 단위)
     * @param dependentsCnt 부양가족 수
     * @param year 적용연도
     * @return 소득세 계산 결과
     */
    public IncomeTaxCalculationResult calculate(int taxableSalary, int dependentsCnt, String year) {
        // 간이세액조견표에서 소득세 조회
        BigDecimal incomeTax = incomeTaxTableRepository
                .findByYearAndSalaryAndDependents(year, taxableSalary, dependentsCnt)
                .map(IncomeTaxTable::getTaxAmount)
                .orElse(BigDecimal.ZERO);

        // 지방소득세 = 소득세 × 10%
        BigDecimal localIncomeTax = incomeTax.multiply(LOCAL_TAX_RATE);

        // 10원 미만 절사
        incomeTax = truncate(incomeTax);
        localIncomeTax = truncate(localIncomeTax);

        return IncomeTaxCalculationResult.of(incomeTax, localIncomeTax);
    }

    /**
     * 소득세 및 지방소득세 계산 (BigDecimal 월급여)
     *
     * @param taxableSalary 과세 대상 월급여 (비과세 제외, 원 단위)
     * @param dependentsCnt 부양가족 수
     * @param year 적용연도
     * @return 소득세 계산 결과
     */
    public IncomeTaxCalculationResult calculate(BigDecimal taxableSalary, int dependentsCnt, String year) {
        // 원 단위 → 천원 단위 변환 (내림)
        int salaryInThousand = taxableSalary
                .divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN)
                .intValue();

        return calculate(salaryInThousand, dependentsCnt, year);
    }

    /**
     * 소득세만 조회 (지방소득세 미포함)
     */
    public BigDecimal getIncomeTax(int taxableSalary, int dependentsCnt, String year) {
        return calculate(taxableSalary, dependentsCnt, year).incomeTax();
    }

    /**
     * 지방소득세만 조회
     */
    public BigDecimal getLocalIncomeTax(int taxableSalary, int dependentsCnt, String year) {
        return calculate(taxableSalary, dependentsCnt, year).localIncomeTax();
    }

    /**
     * 10원 미만 절사
     */
    private BigDecimal truncate(BigDecimal amount) {
        return amount
                .divide(BigDecimal.valueOf(TRUNCATE_UNIT), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(TRUNCATE_UNIT));
    }
}
