package com.workus.workus.payroll.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 급여대상 공제항목 (deduction_detail)
 */
@Entity
@Table(name = "deduction_detail",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_deduction_detail",
           columnNames = {"store_user_id", "accrual_start_date", "accrual_end_date", "deduction_item_code_id"}
       ))
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class DeductionDetail extends BaseEntity {

    @Id
    @Column(name = "deduction_detail_id")
    private Long id;

    @Column(name = "store_user_id")
    private Long storeUserId;

    @Embedded
    private AccrualPeriod accrualPeriod;

    @Column(name = "deduction_item_code_id", length = 20)
    private String deductionItemCodeId;  // 기초코드 참조

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "deduction_rate", precision = 10, scale = 4)
    private BigDecimal deductionRate;

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

    public static DeductionDetail of(
            Long storeUserId,
            AccrualPeriod accrualPeriod,
            String deductionItemCodeId,
            BigDecimal amount,
            BigDecimal deductionRate,
            String formula,
            String formulaText,
            String remarks) {
        return DeductionDetail.builder()
                .id(IdGenerator.nextId())
                .storeUserId(storeUserId)
                .accrualPeriod(accrualPeriod)
                .deductionItemCodeId(deductionItemCodeId)
                .amount(amount)
                .deductionRate(deductionRate)
                .formula(formula)
                .formulaText(formulaText)
                .remarks(remarks)
                .build();
    }

    public static DeductionDetail of(
            Long storeUserId,
            AccrualPeriod accrualPeriod,
            String deductionItemCodeId,
            BigDecimal amount,
            BigDecimal deductionRate) {
        return of(storeUserId, accrualPeriod, deductionItemCodeId, amount, deductionRate, null, null, null);
    }

    public BigDecimal getDeductionAmount() {
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
