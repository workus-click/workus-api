package com.workus.workus.payroll.employee.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 월급제 급여 정보
 * - 시급 환산 시 최저시급 이상이어야 함
 */
@Entity
@DiscriminatorValue("MONTHLY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyWorkerPay extends WorkerPay {

    @Column(name = "monthly_salary")
    private BigDecimal monthlySalary;

    @Builder(access = AccessLevel.PRIVATE)
    private MonthlyWorkerPay(
            Long id,
            Long storeUserId,
            Integer workHoursPerDay,
            Integer workDaysPerWeek,
            Integer dependentsCnt,
            Nationality nationality,
            BigDecimal monthlySalary) {
        super(id, storeUserId, workHoursPerDay, workDaysPerWeek, dependentsCnt, nationality);
        this.monthlySalary = monthlySalary;
    }

    public static MonthlyWorkerPay of(
            Long storeUserId,
            BigDecimal monthlySalary,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        MonthlyWorkerPay pay = MonthlyWorkerPay.builder()
                .storeUserId(storeUserId)
                .monthlySalary(monthlySalary)
                .workHoursPerDay(workHoursPerDay)
                .workDaysPerWeek(workDaysPerWeek)
                .nationality(Nationality.KR)
                .build();
        
        // 시급 환산 후 최저시급 검증
        pay.validateMinimumHourlyRate(pay.calculateHourlyRate());
        
        return MonthlyWorkerPay.builder()
                .id(pay.generateId())
                .storeUserId(storeUserId)
                .monthlySalary(monthlySalary)
                .workHoursPerDay(workHoursPerDay)
                .workDaysPerWeek(workDaysPerWeek)
                .nationality(Nationality.KR)
                .build();
    }

    @Override
    public BigDecimal calculateMonthlySalary() {
        return monthlySalary != null ? monthlySalary : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateHourlyRate() {
        if (monthlySalary == null) {
            return BigDecimal.ZERO;
        }
        int monthlyHours = calculateMonthlyWorkHours();
        if (monthlyHours == 0) {
            return BigDecimal.ZERO;
        }
        return monthlySalary.divide(BigDecimal.valueOf(monthlyHours), 2, RoundingMode.FLOOR);
    }

    @Override
    public PayType getPayType() {
        return PayType.MONTHLY;
    }

    public void updateMonthlySalary(BigDecimal monthlySalary) {
        this.monthlySalary = monthlySalary;
        // 시급 환산 후 최저시급 검증
        validateMinimumHourlyRate(calculateHourlyRate());
    }
}
