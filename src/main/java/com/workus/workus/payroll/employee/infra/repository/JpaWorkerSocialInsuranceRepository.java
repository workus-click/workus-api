package com.workus.workus.payroll.employee.infra.repository;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaWorkerSocialInsuranceRepository extends JpaRepository<WorkerSocialInsurance, Long> {

    Optional<WorkerSocialInsurance> findByStoreUserIdAndInsuranceType(Long storeUserId, SocialInsuranceType insuranceType);

    List<WorkerSocialInsurance> findAllByStoreUserId(Long storeUserId);

    void deleteAllByStoreUserId(Long storeUserId);
}
