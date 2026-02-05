package com.workus.workus.payroll.infra.repository;

import com.workus.workus.payroll.domain.model.AccrualPeriod;
import com.workus.workus.payroll.domain.model.PayrollCalculationResult;
import com.workus.workus.payroll.domain.repository.PayrollResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PayrollResultRepositoryImpl implements PayrollResultRepository {

    private final JpaPayrollResultRepository jpaRepository;

    @Override
    public Optional<PayrollCalculationResult> findByStoreUserIdAndAccrualPeriod(Long storeUserId, AccrualPeriod accrualPeriod) {
        return jpaRepository.findByStoreUserIdAndAccrualPeriod(
                storeUserId, accrualPeriod.getStartDate(), accrualPeriod.getEndDate());
    }

    @Override
    public Optional<PayrollCalculationResult> findWithDetails(Long storeUserId, AccrualPeriod accrualPeriod) {
        return jpaRepository.findWithDetails(
                storeUserId, accrualPeriod.getStartDate(), accrualPeriod.getEndDate());
    }

    @Override
    public List<PayrollCalculationResult> findAllByStoreUserId(Long storeUserId) {
        return jpaRepository.findAllByStoreUserId(storeUserId);
    }

    @Override
    public List<PayrollCalculationResult> findAllByYearMonth(YearMonth yearMonth) {
        return jpaRepository.findAllByYearMonth(yearMonth.getYear(), yearMonth.getMonthValue());
    }

    @Override
    public List<PayrollCalculationResult> findAllByPeriodOverlap(AccrualPeriod period) {
        return jpaRepository.findAllByPeriodOverlap(period.getStartDate(), period.getEndDate());
    }

    @Override
    public PayrollCalculationResult save(PayrollCalculationResult result) {
        return jpaRepository.save(result);
    }

    @Override
    public void delete(PayrollCalculationResult result) {
        jpaRepository.delete(result);
    }

    @Override
    public boolean existsByStoreUserIdAndAccrualPeriod(Long storeUserId, AccrualPeriod accrualPeriod) {
        return jpaRepository.existsByStoreUserIdAndAccrualPeriod(
                storeUserId, accrualPeriod.getStartDate(), accrualPeriod.getEndDate());
    }
}
