package com.workus.workus.payroll.infra.repository;

import com.workus.workus.payroll.domain.model.PayrollCalculationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaPayrollResultRepository extends JpaRepository<PayrollCalculationResult, Long> {

    @Query("SELECT p FROM PayrollCalculationResult p " +
           "WHERE p.storeUserId = :storeUserId " +
           "AND p.accrualPeriod.startDate = :startDate " +
           "AND p.accrualPeriod.endDate = :endDate")
    Optional<PayrollCalculationResult> findByStoreUserIdAndAccrualPeriod(
            @Param("storeUserId") Long storeUserId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM PayrollCalculationResult p " +
           "LEFT JOIN FETCH p.payrollDetails " +
           "LEFT JOIN FETCH p.deductionDetails " +
           "WHERE p.storeUserId = :storeUserId " +
           "AND p.accrualPeriod.startDate = :startDate " +
           "AND p.accrualPeriod.endDate = :endDate")
    Optional<PayrollCalculationResult> findWithDetails(
            @Param("storeUserId") Long storeUserId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<PayrollCalculationResult> findAllByStoreUserId(Long storeUserId);

    /**
     * 특정 연월에 시작일이 포함되는 급여 기록 조회
     */
    @Query("SELECT p FROM PayrollCalculationResult p " +
           "WHERE YEAR(p.accrualPeriod.startDate) = :year " +
           "AND MONTH(p.accrualPeriod.startDate) = :month")
    List<PayrollCalculationResult> findAllByYearMonth(
            @Param("year") int year,
            @Param("month") int month);

    /**
     * 특정 기간에 겹치는 급여 기록 조회
     */
    @Query("SELECT p FROM PayrollCalculationResult p " +
           "WHERE p.accrualPeriod.startDate <= :endDate " +
           "AND p.accrualPeriod.endDate >= :startDate")
    List<PayrollCalculationResult> findAllByPeriodOverlap(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM PayrollCalculationResult p " +
           "WHERE p.storeUserId = :storeUserId " +
           "AND p.accrualPeriod.startDate = :startDate " +
           "AND p.accrualPeriod.endDate = :endDate")
    boolean existsByStoreUserIdAndAccrualPeriod(
            @Param("storeUserId") Long storeUserId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
