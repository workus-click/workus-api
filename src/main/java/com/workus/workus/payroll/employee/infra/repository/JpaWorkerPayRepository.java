package com.workus.workus.payroll.employee.infra.repository;

import com.workus.workus.payroll.employee.domain.model.WorkerPay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaWorkerPayRepository extends JpaRepository<WorkerPay, Long> {

    Optional<WorkerPay> findByStoreUserId(Long storeUserId);

    void deleteByStoreUserId(Long storeUserId);
}
