package com.workus.workus.payroll.employee.domain.repository;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;

import java.util.List;
import java.util.Optional;

public interface WorkerSocialInsuranceRepository {

    Optional<WorkerSocialInsurance> findById(Long id);

    Optional<WorkerSocialInsurance> findByStoreUserIdAndInsuranceType(Long storeUserId, SocialInsuranceType insuranceType);

    List<WorkerSocialInsurance> findAllByStoreUserId(Long storeUserId);

    WorkerSocialInsurance save(WorkerSocialInsurance insurance);

    void delete(WorkerSocialInsurance insurance);

    void deleteAllByStoreUserId(Long storeUserId);
}
