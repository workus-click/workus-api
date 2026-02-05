package com.workus.workus.payroll.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 급여 계산 결과 (payroll)
 */
@Entity
@Table(name = "payroll",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_payroll",
           columnNames = {"store_user_id", "accrual_start_date", "accrual_end_date"}
       ))
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PayrollCalculationResult extends BaseEntity {

    @Id
    @Column(name = "payroll_id")
    private Long id;

    @Column(name = "store_user_id")
    private Long storeUserId;

    @Embedded
    private AccrualPeriod accrualPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_cycle", length = 20)
    private PayCycle payCycle;

    @Column(name = "total_dependents_cnt")
    private Integer totalDependentsCnt;

    @Column(name = "total_taxable_amount", precision = 15, scale = 2)
    private BigDecimal totalTaxableAmount;

    @Column(name = "total_deduction_amount", precision = 15, scale = 2)
    private BigDecimal totalDeductionAmount;

    @Column(name = "net_pay_amount", precision = 15, scale = 2)
    private BigDecimal netPayAmount;

    @Column(name = "is_payslip_sent")
    private Boolean isPayslipSent;

    @Column(name = "payslip_sent_datetime")
    private LocalDateTime payslipSentDatetime;

    @Column(name = "formula_version_id")
    private Long formulaVersionId;

    @OneToMany(mappedBy = "payrollResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PayrollDetail> payrollDetails = new ArrayList<>();

    @OneToMany(mappedBy = "payrollResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeductionDetail> deductionDetails = new ArrayList<>();

    public static PayrollCalculationResult of(
            Long storeUserId,
            AccrualPeriod accrualPeriod,
            PayCycle payCycle,
            Integer totalDependentsCnt,
            BigDecimal totalTaxableAmount,
            BigDecimal totalDeductionAmount,
            BigDecimal netPayAmount,
            Long formulaVersionId) {
        return PayrollCalculationResult.builder()
                .id(IdGenerator.nextId())
                .storeUserId(storeUserId)
                .accrualPeriod(accrualPeriod)
                .payCycle(payCycle)
                .totalDependentsCnt(totalDependentsCnt)
                .totalTaxableAmount(totalTaxableAmount)
                .totalDeductionAmount(totalDeductionAmount)
                .netPayAmount(netPayAmount)
                .isPayslipSent(false)
                .formulaVersionId(formulaVersionId)
                .build();
    }

    /**
     * 월급용 간편 생성
     */
    public static PayrollCalculationResult ofMonthly(
            Long storeUserId,
            int year,
            int month,
            Integer totalDependentsCnt,
            BigDecimal totalTaxableAmount,
            BigDecimal totalDeductionAmount,
            BigDecimal netPayAmount,
            Long formulaVersionId) {
        return of(
                storeUserId,
                AccrualPeriod.ofMonth(year, month),
                PayCycle.MONTHLY,
                totalDependentsCnt,
                totalTaxableAmount,
                totalDeductionAmount,
                netPayAmount,
                formulaVersionId
        );
    }

    // ========== 연관관계 메서드 ==========

    public void addPayrollDetail(PayrollDetail detail) {
        payrollDetails.add(detail);
        detail.setPayrollResult(this);
    }

    public void addDeductionDetail(DeductionDetail detail) {
        deductionDetails.add(detail);
        detail.setPayrollResult(this);
    }

    public void markPayslipSent() {
        this.isPayslipSent = true;
        this.payslipSentDatetime = LocalDateTime.now();
    }

    /**
     * 총 지급액 계산
     */
    public BigDecimal getTotalEarningAmount() {
        return payrollDetails.stream()
                .map(PayrollDetail::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ========== AccrualPeriod 위임 메서드  ==========

    public LocalDate getAccrualStartDate() {
        return accrualPeriod.getStartDate();
    }

    public LocalDate getAccrualEndDate() {
        return accrualPeriod.getEndDate();
    }

    public String getAccrualYearMonth() {
        return accrualPeriod.getYearMonth();
    }

    public String getAccrualYearMonthText() {
        return accrualPeriod.getYearMonthText();
    }

    public String getWeekText() {
        return accrualPeriod.getWeekText();
    }
}
