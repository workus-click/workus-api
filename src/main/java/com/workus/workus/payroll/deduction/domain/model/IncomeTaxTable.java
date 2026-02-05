package com.workus.workus.payroll.deduction.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 간이세액조견표
 * - 급여 구간과 부양가족 수에 따른 소득세액 예상액 조회용
 * - 조회 전용 테이블 (애플리케이션에서 INSERT/UPDATE 하지 않음)
 * - 연 1회 국세청 간이세액표 개정 시 SQL로 직접 데이터 입력
 * - AUTO_INCREMENT 사용: 사용자가 유연하게 추가하는 데이터가 아니므로 Flyway SQL로 관리
 */
@Entity
@Table(name = "income_tax_table",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_income_tax_table",
           columnNames = {"year", "salary_from", "salary_to", "total_dependents_cnt"}
       ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeTaxTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_tax_table_id")
    private Long id;

    @Column(name = "year", length = 4)
    private String year;

    @Column(name = "salary_from")
    private Integer salaryFrom;

    @Column(name = "salary_to")
    private Integer salaryTo;

    @Column(name = "total_dependents_cnt")
    private Integer totalDependentsCnt;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    /**
     * 해당 급여가 이 구간에 포함되는지 확인
     */
    public boolean containsSalary(int salary) {
        return salary >= salaryFrom && salary <= salaryTo;
    }
}
