package com.workus.workus.payroll.employee.domain.service;

import com.workus.workus.payroll.employee.domain.model.HourlyWorkerPay;
import com.workus.workus.payroll.employee.domain.model.MonthlyWorkerPay;
import com.workus.workus.payroll.employee.domain.model.PayType;
import com.workus.workus.payroll.employee.domain.model.WorkerPay;
import com.workus.workus.payroll.employee.domain.repository.WorkerPayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkerPayService {

    private final WorkerPayRepository workerPayRepository;

    /**
     * 노동자 급여 정보 조회
     */
    public Optional<WorkerPay> getWorkerPay(Long storeUserId) {
        return workerPayRepository.findByStoreUserId(storeUserId);
    }

    /**
     * 시급제 급여 정보 생성
     */
    @Transactional
    public HourlyWorkerPay createHourlyPay(
            Long storeUserId,
            BigDecimal hourlyRate,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        
        // 기존 급여 정보가 있으면 예외
        workerPayRepository.findByStoreUserId(storeUserId)
                .ifPresent(existing -> {
                    throw new IllegalStateException("이미 급여 정보가 존재합니다. 변경하려면 changeToHourly를 사용하세요.");
                });

        HourlyWorkerPay workerPay = HourlyWorkerPay.of(storeUserId, hourlyRate, workHoursPerDay, workDaysPerWeek);
        return (HourlyWorkerPay) workerPayRepository.save(workerPay);
    }

    /**
     * 월급제 급여 정보 생성
     */
    @Transactional
    public MonthlyWorkerPay createMonthlyPay(
            Long storeUserId,
            BigDecimal monthlySalary,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        // 기존 급여 정보가 있으면 예외
        workerPayRepository.findByStoreUserId(storeUserId)
                .ifPresent(existing -> {
                    throw new IllegalStateException("이미 급여 정보가 존재합니다. 변경하려면 changeToMonthly를 사용하세요.");
                });

        MonthlyWorkerPay workerPay = MonthlyWorkerPay.of(storeUserId, monthlySalary, workHoursPerDay, workDaysPerWeek);
        return (MonthlyWorkerPay) workerPayRepository.save(workerPay);
    }

    /**
     * 시급제로 변경 (기존 급여 정보 삭제 후 재생성)
     */
    @Transactional
    public HourlyWorkerPay changeToHourly(
            Long storeUserId,
            BigDecimal hourlyRate,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        
        // 기존 급여 정보 삭제
        workerPayRepository.findByStoreUserId(storeUserId)
                .ifPresent(workerPayRepository::delete);

        // 새로운 시급제 정보 생성
        HourlyWorkerPay workerPay = HourlyWorkerPay.of(storeUserId, hourlyRate, workHoursPerDay, workDaysPerWeek);
        return (HourlyWorkerPay) workerPayRepository.save(workerPay);
    }

    /**
     * 월급제로 변경 (기존 급여 정보 삭제 후 재생성)
     */
    @Transactional
    public MonthlyWorkerPay changeToMonthly(
            Long storeUserId,
            BigDecimal monthlySalary,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        // 기존 급여 정보 삭제
        workerPayRepository.findByStoreUserId(storeUserId)
                .ifPresent(workerPayRepository::delete);

        // 새로운 월급제 정보 생성
        MonthlyWorkerPay workerPay = MonthlyWorkerPay.of(storeUserId, monthlySalary, workHoursPerDay, workDaysPerWeek);
        return (MonthlyWorkerPay) workerPayRepository.save(workerPay);
    }

    /**
     * 시급제 정보 수정 (타입 변경 없이)
     */
    @Transactional
    public HourlyWorkerPay updateHourlyPay(
            Long storeUserId,
            BigDecimal hourlyRate,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        
        WorkerPay workerPay = workerPayRepository.findByStoreUserId(storeUserId)
                .orElseThrow(() -> new IllegalArgumentException("급여 정보를 찾을 수 없습니다."));

        if (workerPay.getPayType() != PayType.HOURLY) {
            throw new IllegalStateException("시급제가 아닙니다. 타입 변경은 changeToHourly를 사용하세요.");
        }

        HourlyWorkerPay hourlyWorkerPay = (HourlyWorkerPay) workerPay;
        hourlyWorkerPay.updateHourlyRate(hourlyRate);
        hourlyWorkerPay.updateWorkSchedule(workHoursPerDay, workDaysPerWeek);
        return hourlyWorkerPay;
    }

    /**
     * 월급제 정보 수정 (타입 변경 없이)
     */
    @Transactional
    public MonthlyWorkerPay updateMonthlyPay(
            Long storeUserId,
            BigDecimal monthlySalary,
            Integer workHoursPerDay,
            Integer workDaysPerWeek) {
        WorkerPay workerPay = workerPayRepository.findByStoreUserId(storeUserId)
                .orElseThrow(() -> new IllegalArgumentException("급여 정보를 찾을 수 없습니다."));

        if (workerPay.getPayType() != PayType.MONTHLY) {
            throw new IllegalStateException("월급제가 아닙니다. 타입 변경은 changeToMonthly를 사용하세요.");
        }

        MonthlyWorkerPay monthlyWorkerPay = (MonthlyWorkerPay) workerPay;
        monthlyWorkerPay.updateWorkSchedule(workHoursPerDay, workDaysPerWeek);
        monthlyWorkerPay.updateMonthlySalary(monthlySalary);  // 내부에서 최저시급 검증
        return monthlyWorkerPay;
    }

    /**
     * 부양가족 수 수정
     */
    @Transactional
    public WorkerPay updateDependentsCnt(Long storeUserId, Integer dependentsCnt) {
        WorkerPay workerPay = workerPayRepository.findByStoreUserId(storeUserId)
                .orElseThrow(() -> new IllegalArgumentException("급여 정보를 찾을 수 없습니다."));

        workerPay.updateDependentsCnt(dependentsCnt);
        return workerPay;
    }

    /**
     * 급여 정보 삭제
     */
    @Transactional
    public void deleteWorkerPay(Long storeUserId) {
        workerPayRepository.deleteByStoreUserId(storeUserId);
    }

    /**
     * 월 급여 계산
     */
    public BigDecimal calculateMonthlySalary(Long storeUserId) {
        return workerPayRepository.findByStoreUserId(storeUserId)
                .map(WorkerPay::calculateMonthlySalary)
                .orElse(BigDecimal.ZERO);
    }
}
