package com.workus.workus.payroll.employee.infra.repository;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.employee.domain.repository.WorkerSocialInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkerSocialInsuranceRepositoryImpl implements WorkerSocialInsuranceRepository {

    private final JpaWorkerSocialInsuranceRepository jpaRepository;

    @Override
    public Optional<WorkerSocialInsurance> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<WorkerSocialInsurance> findByStoreUserIdAndInsuranceType(Long storeUserId, SocialInsuranceType insuranceType) {
        return jpaRepository.findByStoreUserIdAndInsuranceType(storeUserId, insuranceType);
    }

    @Override
    public List<WorkerSocialInsurance> findAllByStoreUserId(Long storeUserId) {
        return jpaRepository.findAllByStoreUserId(storeUserId);
    }

    @Override
    public WorkerSocialInsurance save(WorkerSocialInsurance insurance) {
        return jpaRepository.save(insurance);
    }

    @Override
    public void delete(WorkerSocialInsurance insurance) {
        jpaRepository.delete(insurance);
    }

    @Override
    public void deleteAllByStoreUserId(Long storeUserId) {
        jpaRepository.deleteAllByStoreUserId(storeUserId);
    }
}
