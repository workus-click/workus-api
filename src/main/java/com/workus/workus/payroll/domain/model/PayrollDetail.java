package com.workus.workus.payroll.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 급여대상 지급항목 (payroll_detail)
 */
@Entity
@Table(name = "payroll_detail",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_payroll_detail",
           columnNames = {"store_user_id", "accrual_start_date", "accrual_end_date", "salary_item_code_id"}
       ))
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PayrollDetail extends BaseEntity {

    @Id
    @Column(name = "payroll_detail_id")
    private Long id;

    @Column(name = "store_user_id")
    private Long storeUserId;

    @Embedded
    private AccrualPeriod accrualPeriod;

    @Column(name = "salary_item_code_id")
    private Long salaryItemCodeId;  // 기초코드 참조

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "base_time", precision = 10, scale = 2)
    private BigDecimal baseTime;

    @Column(name = "pay_rate", precision = 10, scale = 4)
    private BigDecimal payRate;

    @Column(name = "formula", length = 500)
    private String formula;

    @Column(name = "formula_text", length = 500)
    private String formulaText;

    @Column(name = "remarks", length = 200)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id")
    @Setter(AccessLevel.PACKAGE)
    private PayrollCalculationResult payrollResult;

    public static PayrollDetail of(
            Long storeUserId,
            AccrualPeriod accrualPeriod,
            Long salaryItemCodeId,
            BigDecimal amount,
            BigDecimal baseTime,
            BigDecimal payRate,
            String formula,
            String formulaText,
            String remarks) {
        return PayrollDetail.builder()
                .id(IdGenerator.nextId())
                .storeUserId(storeUserId)
                .accrualPeriod(accrualPeriod)
                .salaryItemCodeId(salaryItemCodeId)
                .amount(amount)
                .baseTime(baseTime)
                .payRate(payRate != null ? payRate : BigDecimal.ONE)
                .formula(formula)
                .formulaText(formulaText)
                .remarks(remarks)
                .build();
    }

    public static PayrollDetail of(
            Long storeUserId,
            AccrualPeriod accrualPeriod,
            Long salaryItemCodeId,
            BigDecimal amount) {
        return of(storeUserId, accrualPeriod, salaryItemCodeId, amount, null, null, null, null, null);
    }

    public BigDecimal getPayAmount() {
        return amount;
    }

    // ========== AccrualPeriod 위임 메서드 (편의용) ==========

    public LocalDate getAccrualStartDate() {
        return accrualPeriod.getStartDate();
    }

    public LocalDate getAccrualEndDate() {
        return accrualPeriod.getEndDate();
    }
}
