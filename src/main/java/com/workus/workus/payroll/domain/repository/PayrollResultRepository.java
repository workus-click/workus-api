package com.workus.workus.payroll.domain.repository;

import com.workus.workus.payroll.domain.model.AccrualPeriod;
import com.workus.workus.payroll.domain.model.PayrollCalculationResult;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PayrollResultRepository {

    /**
     * 메인 테이블만 조회 (정확한 기간)
     */
    Optional<PayrollCalculationResult> findByStoreUserIdAndAccrualPeriod(Long storeUserId, AccrualPeriod accrualPeriod);

    /**
     * 상세 포함 조회 (fetch join)
     */
    Optional<PayrollCalculationResult> findWithDetails(Long storeUserId, AccrualPeriod accrualPeriod);

    /**
     * 특정 사용자의 전체 급여 이력 조회 (메인만)
     */
    List<PayrollCalculationResult> findAllByStoreUserId(Long storeUserId);

    /**
     * 특정 연월에 해당하는 급여 대상자 조회 (시작일 기준)
     * 해당 월에 시작일이 포함되는 모든 급여 기록 조회
     */
    List<PayrollCalculationResult> findAllByYearMonth(YearMonth yearMonth);

    /**
     * 특정 기간에 겹치는 급여 대상자 조회
     * 조회 기간과 귀속 기간이 겹치는 모든 급여 기록 조회
     */
    List<PayrollCalculationResult> findAllByPeriodOverlap(AccrualPeriod period);

    /**
     * 저장
     */
    PayrollCalculationResult save(PayrollCalculationResult result);

    /**
     * 삭제
     */
    void delete(PayrollCalculationResult result);

    /**
     * 존재 여부 확인
     */
    boolean existsByStoreUserIdAndAccrualPeriod(Long storeUserId, AccrualPeriod accrualPeriod);
}
