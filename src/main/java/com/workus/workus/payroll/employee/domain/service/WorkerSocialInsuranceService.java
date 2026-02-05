package com.workus.workus.payroll.employee.domain.service;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.employee.domain.repository.WorkerSocialInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkerSocialInsuranceService {

    private final WorkerSocialInsuranceRepository insuranceRepository;

    /**
     * 노동자의 모든 사회보험 정보 조회
     */
    public List<WorkerSocialInsurance> getInsuranceList(Long storeUserId) {
        return insuranceRepository.findAllByStoreUserId(storeUserId);
    }

    /**
     * 노동자의 사회보험 가입 여부를 Map으로 조회
     */
    public Map<SocialInsuranceType, Boolean> getInsuranceMap(Long storeUserId) {
        List<WorkerSocialInsurance> insurances = insuranceRepository.findAllByStoreUserId(storeUserId);
        
        Map<SocialInsuranceType, Boolean> result = new EnumMap<>(SocialInsuranceType.class);
        for (SocialInsuranceType type : SocialInsuranceType.values()) {
            result.put(type, false);
        }
        
        for (WorkerSocialInsurance insurance : insurances) {
            result.put(insurance.getInsuranceType(), insurance.isEnrolled());
        }
        
        return result;
    }

    /**
     * 특정 보험 가입 여부 조회
     */
    public boolean isEnrolled(Long storeUserId, SocialInsuranceType insuranceType) {
        return insuranceRepository.findByStoreUserIdAndInsuranceType(storeUserId, insuranceType)
                .map(WorkerSocialInsurance::isEnrolled)
                .orElse(false);
    }

    /**
     * 사회보험 가입 정보 일괄 설정
     */
    @Transactional
    public void updateInsurances(Long storeUserId, Set<SocialInsuranceType> enrolledTypes) {
        for (SocialInsuranceType type : SocialInsuranceType.values()) {
            boolean enrolled = enrolledTypes.contains(type);
            updateInsurance(storeUserId, type, enrolled);
        }
    }

    /**
     * 특정 보험 가입 여부 설정
     */
    @Transactional
    public WorkerSocialInsurance updateInsurance(Long storeUserId, SocialInsuranceType insuranceType, boolean enrolled) {
        return insuranceRepository.findByStoreUserIdAndInsuranceType(storeUserId, insuranceType)
                .map(insurance -> {
                    insurance.updateEnrolled(enrolled);
                    return insurance;
                })
                .orElseGet(() -> {
                    WorkerSocialInsurance newInsurance = WorkerSocialInsurance.of(storeUserId, insuranceType, enrolled);
                    return insuranceRepository.save(newInsurance);
                });
    }

    /**
     * 노동자의 모든 사회보험 정보 삭제
     */
    @Transactional
    public void deleteAllByStoreUserId(Long storeUserId) {
        insuranceRepository.deleteAllByStoreUserId(storeUserId);
    }
}
