package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.employee.domain.model.Nationality;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.deduction.domain.model.InsuranceAmount;
import com.workus.workus.payroll.deduction.domain.model.SocialInsuranceCalculationResult;
import com.workus.workus.payroll.deduction.domain.model.SocialInsuranceRate;
import com.workus.workus.payroll.deduction.domain.repository.SocialInsuranceRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사회보험료 계산 서비스
 * 
 * - SocialInsuranceEligibilityService를 통해 적용 여부 판단
 * - 적용 대상인 경우 요율을 적용하여 직원/회사 부담금 계산
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialInsuranceCalculationService {

    private final SocialInsuranceEligibilityService eligibilityService;
    private final SocialInsuranceRateRepository socialInsuranceRateRepository;

    /**
     * 4대보험료 계산
     *
     * @param storeUserId 매장 사용자 ID
     * @param monthlySalary 월급여
     * @param age 근로자 나이 (만 나이)
     * @param contractMonths 근로계약 기간 (개월)
     * @param weeklyWorkHours 주당 근무시간
     * @param nationality 국적
     * @param year 적용연도
     * @return 사회보험 계산 결과
     */
    public SocialInsuranceCalculationResult calculate(
            Long storeUserId,
            BigDecimal monthlySalary,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality,
            String year) {

        // 1. 적용 가능한 보험 목록 조회 (회사 체크 + 근로자 조건)
        Set<SocialInsuranceType> eligibleInsurances = eligibilityService.getEligibleInsurances(
                storeUserId, age, contractMonths, weeklyWorkHours, nationality);

        // 2. 연도별 보험 요율 조회
        Map<String, SocialInsuranceRate> rateMap = socialInsuranceRateRepository
                .findAllByYear(year).stream()
                .collect(Collectors.toMap(SocialInsuranceRate::getInsuranceType, rate -> rate));

        // 3. 각 보험별 계산
        Map<SocialInsuranceType, InsuranceAmount> details = new EnumMap<>(SocialInsuranceType.class);

        for (SocialInsuranceType insuranceType : SocialInsuranceType.values()) {
            InsuranceAmount amount = calculateInsurance(
                    insuranceType,
                    monthlySalary,
                    eligibleInsurances,
                    rateMap
            );
            details.put(insuranceType, amount);
        }

        return SocialInsuranceCalculationResult.of(details);
    }

    /**
     * 개별 보험료 계산
     */
    private InsuranceAmount calculateInsurance(
            SocialInsuranceType insuranceType,
            BigDecimal monthlySalary,
            Set<SocialInsuranceType> eligibleInsurances,
            Map<String, SocialInsuranceRate> rateMap) {

        // 적용 대상이 아닌 경우
        if (!eligibleInsurances.contains(insuranceType)) {
            return InsuranceAmount.notApplicable();
        }

        // 요율 조회
        SocialInsuranceRate rate = rateMap.get(insuranceType.name());
        if (rate == null) {
            // 요율 정보가 없으면 계산 불가
            return InsuranceAmount.notApplicable();
        }

        // 장기요양보험은 건강보험료 기준으로 계산
        if (insuranceType == SocialInsuranceType.LONG_TERM_CARE_INSURANCE) {
            return calculateLongTermCareInsurance(monthlySalary, rateMap);
        }

        // 보험료 계산 (기준소득 상/하한 적용)
        BigDecimal employeeAmount = rate.calculateEmployeeAmount(monthlySalary)
                .setScale(0, RoundingMode.FLOOR);
        BigDecimal employerAmount = rate.calculateEmployerAmount(monthlySalary)
                .setScale(0, RoundingMode.FLOOR);

        return InsuranceAmount.of(employeeAmount, employerAmount);
    }

    /**
     * 장기요양보험료 계산 (건강보험료의 일정 비율)
     */
    private InsuranceAmount calculateLongTermCareInsurance(
            BigDecimal monthlySalary,
            Map<String, SocialInsuranceRate> rateMap) {

        SocialInsuranceRate healthRate = rateMap.get(SocialInsuranceType.HEALTH_INSURANCE.name());
        SocialInsuranceRate longTermCareRate = rateMap.get(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.name());

        if (healthRate == null || longTermCareRate == null) {
            return InsuranceAmount.notApplicable();
        }

        // 건강보험료 계산
        BigDecimal healthEmployeeAmount = healthRate.calculateEmployeeAmount(monthlySalary);
        BigDecimal healthEmployerAmount = healthRate.calculateEmployerAmount(monthlySalary);

        // 장기요양보험료 = 건강보험료 × 장기요양보험 요율
        BigDecimal employeeAmount = healthEmployeeAmount
                .multiply(longTermCareRate.getEmployeeRate())
                .setScale(0, RoundingMode.FLOOR);
        BigDecimal employerAmount = healthEmployerAmount
                .multiply(longTermCareRate.getEmployerRate())
                .setScale(0, RoundingMode.FLOOR);

        return InsuranceAmount.of(employeeAmount, employerAmount);
    }
}
