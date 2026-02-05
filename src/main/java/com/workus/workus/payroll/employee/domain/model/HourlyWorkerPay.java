package com.workus.workus.payroll.employee.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 시급제 급여 정보
 * - 월급여 = 시간단가 × 일 근무시간 × 주 근무일수 × 4주
 */
@Entity
@DiscriminatorValue("HOURLY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HourlyWorkerPay extends WorkerPay {

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Builder(access = AccessLevel.PRIVATE)
    private HourlyWorkerPay(
            Long id,
            Long storeUserId,
            Integer workHoursPerDay,
            Integer workDaysPerWeek,
            Integer dependentsCnt,
            Nationality nationality,
            BigDecimal hourlyRate) {
        super(id, storeUserId, workHoursPerDay, workDaysPerWeek, dependentsCnt, nationality);
        this.hourlyRate = hourlyRate;
    }

    public static HourlyWorkerPay of(
            Long storeUserId,
            BigDecimal hourlyRate,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        HourlyWorkerPay pay = HourlyWorkerPay.builder()
                .storeUserId(storeUserId)
                .hourlyRate(hourlyRate)
                .workHoursPerDay(workHoursPerDay)
                .workDaysPerWeek(workDaysPerWeek)
                .nationality(Nationality.KR)
                .build();
        
        // 최저시급 검증
        pay.validateMinimumHourlyRate(hourlyRate);
        
        return HourlyWorkerPay.builder()
                .id(pay.generateId())
                .storeUserId(storeUserId)
                .hourlyRate(hourlyRate)
                .workHoursPerDay(workHoursPerDay)
                .workDaysPerWeek(workDaysPerWeek)
                .nationality(Nationality.KR)
                .build();
    }

    @Override
    public BigDecimal calculateMonthlySalary() {
        if (hourlyRate == null) {
            return BigDecimal.ZERO;
        }
        int monthlyHours = calculateMonthlyWorkHours();
        if (monthlyHours == 0) {
            return BigDecimal.ZERO;
        }
        return hourlyRate.multiply(BigDecimal.valueOf(monthlyHours));
    }

    @Override
    public BigDecimal calculateHourlyRate() {
        return hourlyRate;
    }

    @Override
    public PayType getPayType() {
        return PayType.HOURLY;
    }

    public void updateHourlyRate(BigDecimal hourlyRate) {
        validateMinimumHourlyRate(hourlyRate);
        this.hourlyRate = hourlyRate;
    }
}
