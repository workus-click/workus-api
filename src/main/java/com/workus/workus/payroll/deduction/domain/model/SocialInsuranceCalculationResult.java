package com.workus.workus.payroll.deduction.domain.model;

import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 사회보험 계산 결과
 */
public record SocialInsuranceCalculationResult(
        Map<SocialInsuranceType, InsuranceAmount> details,  // 보험별 상세
        BigDecimal totalEmployeeAmount,                      // 직원 부담 총액
        BigDecimal totalEmployerAmount                       // 회사 부담 총액
) {
    public static SocialInsuranceCalculationResult of(Map<SocialInsuranceType, InsuranceAmount> details) {
        BigDecimal totalEmployee = details.values().stream()
                .filter(InsuranceAmount::applicable)
                .map(InsuranceAmount::employeeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEmployer = details.values().stream()
                .filter(InsuranceAmount::applicable)
                .map(InsuranceAmount::employerAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SocialInsuranceCalculationResult(details, totalEmployee, totalEmployer);
    }

    /**
     * 총 사회보험료 (직원 + 회사)
     */
    public BigDecimal totalAmount() {
        return totalEmployeeAmount.add(totalEmployerAmount);
    }

    /**
     * 특정 보험 적용 여부
     */
    public boolean isApplicable(SocialInsuranceType type) {
        InsuranceAmount amount = details.get(type);
        return amount != null && amount.applicable();
    }

    /**
     * 특정 보험 직원 부담액
     */
    public BigDecimal getEmployeeAmount(SocialInsuranceType type) {
        InsuranceAmount amount = details.get(type);
        return amount != null ? amount.employeeAmount() : BigDecimal.ZERO;
    }

    /**
     * 특정 보험 회사 부담액
     */
    public BigDecimal getEmployerAmount(SocialInsuranceType type) {
        InsuranceAmount amount = details.get(type);
        return amount != null ? amount.employerAmount() : BigDecimal.ZERO;
    }
}
