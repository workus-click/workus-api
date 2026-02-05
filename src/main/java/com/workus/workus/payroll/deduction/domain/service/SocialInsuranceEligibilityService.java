package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.Nationality;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.employee.domain.repository.WorkerSocialInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사회보험 적용 여부 판단 서비스
 * 
 * 판단 기준:
 * 1순위: 회사에서 해당 보험을 사용하겠다고 체크했는지
 * 2순위: 근로자가 해당 보험 적용 대상인지 (나이, 계약기간, 주당근무시간, 국적)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialInsuranceEligibilityService {

    private final WorkerSocialInsuranceRepository workerSocialInsuranceRepository;

    /**
     * 특정 보험 적용 여부 판단
     */
    public boolean isEligible(
            Long storeUserId,
            SocialInsuranceType insuranceType,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality) {

        // 1순위: 회사에서 해당 보험을 사용하겠다고 체크했는지
        if (!isEnrolledByCompany(storeUserId, insuranceType)) {
            return false;
        }

        // 2순위: 근로자가 적용 대상인지
        return insuranceType.isApplicable(age, contractMonths, weeklyWorkHours, nationality);
    }

    /**
     * 전체 보험 적용 여부 판단
     * @return 보험타입 -> 적용여부 Map
     */
    public Map<SocialInsuranceType, Boolean> checkAllEligibility(
            Long storeUserId,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality) {

        // 회사가 체크한 보험 목록 조회
        Set<SocialInsuranceType> enrolledInsurances = getEnrolledInsurances(storeUserId);

        Map<SocialInsuranceType, Boolean> result = new EnumMap<>(SocialInsuranceType.class);

        for (SocialInsuranceType insuranceType : SocialInsuranceType.values()) {
            boolean eligible = enrolledInsurances.contains(insuranceType)
                    && insuranceType.isApplicable(age, contractMonths, weeklyWorkHours, nationality);
            result.put(insuranceType, eligible);
        }

        return result;
    }

    /**
     * 적용 가능한 보험 목록 조회
     */
    public Set<SocialInsuranceType> getEligibleInsurances(
            Long storeUserId,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality) {

        return checkAllEligibility(storeUserId, age, contractMonths, weeklyWorkHours, nationality)
                .entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * 회사에서 해당 보험을 체크했는지 확인
     */
    public boolean isEnrolledByCompany(Long storeUserId, SocialInsuranceType insuranceType) {
        return workerSocialInsuranceRepository
                .findByStoreUserIdAndInsuranceType(storeUserId, insuranceType)
                .map(WorkerSocialInsurance::isEnrolled)
                .orElse(false);
    }

    /**
     * 회사가 체크한 보험 목록 조회
     */
    public Set<SocialInsuranceType> getEnrolledInsurances(Long storeUserId) {
        List<WorkerSocialInsurance> insurances = workerSocialInsuranceRepository
                .findAllByStoreUserId(storeUserId);

        return insurances.stream()
                .filter(WorkerSocialInsurance::isEnrolled)
                .map(WorkerSocialInsurance::getInsuranceType)
                .collect(Collectors.toSet());
    }

    /**
     * 근로자 조건만으로 적용 대상인지 판단 (회사 체크 여부 무관)
     */
    public boolean isWorkerEligible(
            SocialInsuranceType insuranceType,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality) {

        return insuranceType.isApplicable(age, contractMonths, weeklyWorkHours, nationality);
    }

    /**
     * 근로자 조건으로 적용 가능한 보험 목록 (회사 체크 여부 무관)
     */
    public Set<SocialInsuranceType> getWorkerEligibleInsurances(
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality) {

        Set<SocialInsuranceType> result = java.util.EnumSet.noneOf(SocialInsuranceType.class);

        for (SocialInsuranceType insuranceType : SocialInsuranceType.values()) {
            if (insuranceType.isApplicable(age, contractMonths, weeklyWorkHours, nationality)) {
                result.add(insuranceType);
            }
        }

        return result;
    }
}
