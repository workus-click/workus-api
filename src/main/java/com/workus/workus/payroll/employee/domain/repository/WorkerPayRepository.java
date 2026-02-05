package com.workus.workus.payroll.employee.domain.repository;

import com.workus.workus.payroll.employee.domain.model.WorkerPay;

import java.util.Optional;

public interface WorkerPayRepository {

    Optional<WorkerPay> findById(Long id);

    Optional<WorkerPay> findByStoreUserId(Long storeUserId);

    WorkerPay save(WorkerPay workerPay);

    void delete(WorkerPay workerPay);

    void deleteByStoreUserId(Long storeUserId);
}
