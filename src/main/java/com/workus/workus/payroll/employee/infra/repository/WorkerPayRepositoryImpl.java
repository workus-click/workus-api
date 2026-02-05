package com.workus.workus.payroll.employee.infra.repository;

import com.workus.workus.payroll.employee.domain.model.WorkerPay;
import com.workus.workus.payroll.employee.domain.repository.WorkerPayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkerPayRepositoryImpl implements WorkerPayRepository {

    private final JpaWorkerPayRepository jpaRepository;

    @Override
    public Optional<WorkerPay> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<WorkerPay> findByStoreUserId(Long storeUserId) {
        return jpaRepository.findByStoreUserId(storeUserId);
    }

    @Override
    public WorkerPay save(WorkerPay workerPay) {
        return jpaRepository.save(workerPay);
    }

    @Override
    public void delete(WorkerPay workerPay) {
        jpaRepository.delete(workerPay);
    }

    @Override
    public void deleteByStoreUserId(Long storeUserId) {
        jpaRepository.deleteByStoreUserId(storeUserId);
    }
}
