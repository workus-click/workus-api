package com.workus.workus.payroll.deduction.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 사회보험 요율
 * - 연도/보험종류별 사원/회사 부담 요율 조회용
 * - 조회 전용 테이블 (애플리케이션에서 INSERT/UPDATE 하지 않음)
 * - 연 1회 요율 변경 시 SQL로 직접 데이터 입력
 * - AUTO_INCREMENT 사용: 사용자가 유연하게 추가하는 데이터가 아니므로 Flyway SQL로 관리
 */
@Entity
@Table(name = "social_insurance_rates",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_social_insurance_rates",
           columnNames = {"year", "insurance_type"}
       ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInsuranceRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_insurance_rate_id")
    private Long id;

    @Column(name = "year", length = 4)
    private String year;

    @Column(name = "insurance_type", length = 20)
    private String insuranceType;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "employee_rate", precision = 6, scale = 4)
    private BigDecimal employeeRate;

    @Column(name = "employer_rate", precision = 6, scale = 4)
    private BigDecimal employerRate;

    @Column(name = "total_rate", precision = 6, scale = 4)
    private BigDecimal totalRate;

    @Column(name = "min_base_amount")
    private Long minBaseAmount;

    @Column(name = "max_base_amount")
    private Long maxBaseAmount;

    /**
     * 해당 날짜에 적용되는 요율인지 확인
     */
    public boolean isEffectiveOn(LocalDate date) {
        return !date.isBefore(effectiveFrom) && !date.isAfter(effectiveTo);
    }

    /**
     * 기준소득 상/하한 적용
     * - 급여가 하한보다 낮으면 하한 적용
     * - 급여가 상한보다 높으면 상한 적용
     */
    public BigDecimal applyBaseAmountLimit(BigDecimal salary) {
        if (minBaseAmount != null && salary.compareTo(BigDecimal.valueOf(minBaseAmount)) < 0) {
            return BigDecimal.valueOf(minBaseAmount);
        }
        if (maxBaseAmount != null && salary.compareTo(BigDecimal.valueOf(maxBaseAmount)) > 0) {
            return BigDecimal.valueOf(maxBaseAmount);
        }
        return salary;
    }

    /**
     * 급여 기준 사원 부담액 계산 (상/하한 적용)
     */
    public BigDecimal calculateEmployeeAmount(BigDecimal salary) {
        BigDecimal baseSalary = applyBaseAmountLimit(salary);
        return baseSalary.multiply(employeeRate);
    }

    /**
     * 급여 기준 회사 부담액 계산 (상/하한 적용)
     */
    public BigDecimal calculateEmployerAmount(BigDecimal salary) {
        BigDecimal baseSalary = applyBaseAmountLimit(salary);
        return baseSalary.multiply(employerRate);
    }
}
