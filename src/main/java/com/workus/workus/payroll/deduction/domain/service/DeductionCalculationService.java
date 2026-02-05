package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.Nationality;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.employee.domain.repository.WorkerSocialInsuranceRepository;
import com.workus.workus.payroll.deduction.domain.model.*;
import com.workus.workus.payroll.deduction.domain.repository.IncomeTaxTableRepository;
import com.workus.workus.payroll.deduction.domain.repository.SocialInsuranceRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 공제 계산 서비스
 * - DeductionType 순서대로 순회하며 계산
 * - 4대보험: SocialInsuranceRate에서 요율 조회
 * - 소득세: IncomeTaxTable에서 조회
 * - 지방소득세: 소득세 × 10%
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeductionCalculationService {

    private static final BigDecimal LOCAL_TAX_RATE = new BigDecimal("0.1");
    private static final int TRUNCATE_UNIT = 10;

    private final WorkerSocialInsuranceRepository workerSocialInsuranceRepository;
    private final SocialInsuranceRateRepository socialInsuranceRateRepository;
    private final IncomeTaxTableRepository incomeTaxTableRepository;

    /**
     * 전체 공제 계산
     *
     * @param storeUserId 매장 사용자 ID
     * @param monthlySalary 월급여 (원)
     * @param taxableSalary 과세 대상 급여 (비과세 제외, 원)
     * @param age 만 나이
     * @param contractMonths 계약 기간 (개월)
     * @param weeklyWorkHours 주당 근무시간
     * @param dependentsCnt 부양가족 수
     * @param nationality 국적
     * @param year 적용연도
     * @return 공제 계산 결과
     */
    public DeductionCalculationResult calculate(
            Long storeUserId,
            BigDecimal monthlySalary,
            BigDecimal taxableSalary,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            int dependentsCnt,
            Nationality nationality,
            String year) {

        // 회사가 체크한 사회보험 목록
        Set<SocialInsuranceType> enrolledInsurances = getEnrolledInsurances(storeUserId);

        // 사회보험 요율 조회
        Map<String, SocialInsuranceRate> rateMap = socialInsuranceRateRepository
                .findAllByYear(year).stream()
                .collect(Collectors.toMap(SocialInsuranceRate::getInsuranceType, r -> r));

        // 계산 결과 저장 (순서대로 계산하기 위해 LinkedHashMap 사용 가능하지만 EnumMap도 순서 보장)
        Map<DeductionType, DeductionAmount> details = new EnumMap<>(DeductionType.class);
        BigDecimal calculatedIncomeTax = BigDecimal.ZERO;

        // 순서대로 계산
        for (DeductionType type : DeductionType.orderedValues()) {
            DeductionAmount amount;

            // 적용 여부 체크
            if (!isApplicable(type, age, contractMonths, weeklyWorkHours, nationality, enrolledInsurances)) {
                amount = DeductionAmount.notApplicable(type);
            } else {
                // 타입별 계산
                amount = switch (type) {
                    case NATIONAL_PENSION, HEALTH_INSURANCE, EMPLOYMENT_INSURANCE ->
                            calculateSocialInsurance(type, monthlySalary, rateMap);
                    case LONG_TERM_CARE_INSURANCE ->
                            calculateLongTermCare(monthlySalary, rateMap);
                    case INCOME_TAX -> {
                        DeductionAmount incomeTaxAmount = calculateIncomeTax(taxableSalary, dependentsCnt, year);
                        calculatedIncomeTax = incomeTaxAmount.employeeAmount();
                        yield incomeTaxAmount;
                    }
                    case LOCAL_INCOME_TAX ->
                            calculateLocalIncomeTax(calculatedIncomeTax);
                };
            }

            details.put(type, amount);
        }

        return DeductionCalculationResult.of(details);
    }

    /**
     * 적용 여부 판단 (회사 체크 + 근로자 조건)
     */
    private boolean isApplicable(
            DeductionType type,
            int age,
            int contractMonths,
            double weeklyWorkHours,
            Nationality nationality,
            Set<SocialInsuranceType> enrolledInsurances) {

        // 세금은 항상 적용 (근로자 조건만 체크)
        if (type.isTax()) {
            return type.isApplicable(age, contractMonths, weeklyWorkHours, nationality);
        }

        // 사회보험: 회사 체크 여부 + 근로자 조건
        SocialInsuranceType socialType = toSocialInsuranceType(type);
        if (socialType == null || !enrolledInsurances.contains(socialType)) {
            return false;
        }

        return type.isApplicable(age, contractMonths, weeklyWorkHours, nationality);
    }

    /**
     * 사회보험 계산
     */
    private DeductionAmount calculateSocialInsurance(
            DeductionType type,
            BigDecimal monthlySalary,
            Map<String, SocialInsuranceRate> rateMap) {

        SocialInsuranceRate rate = rateMap.get(toSocialInsuranceType(type).name());
        if (rate == null) {
            return DeductionAmount.notApplicable(type);
        }

        BigDecimal employeeAmount = truncate(rate.calculateEmployeeAmount(monthlySalary));
        BigDecimal employerAmount = truncate(rate.calculateEmployerAmount(monthlySalary));

        return DeductionAmount.of(type, employeeAmount, employerAmount);
    }

    /**
     * 장기요양보험 계산 (건강보험료 × 요율)
     */
    private DeductionAmount calculateLongTermCare(
            BigDecimal monthlySalary,
            Map<String, SocialInsuranceRate> rateMap) {

        SocialInsuranceRate healthRate = rateMap.get(SocialInsuranceType.HEALTH_INSURANCE.name());
        SocialInsuranceRate longTermRate = rateMap.get(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.name());

        if (healthRate == null || longTermRate == null) {
            return DeductionAmount.notApplicable(DeductionType.LONG_TERM_CARE_INSURANCE);
        }

        BigDecimal healthEmployeeAmount = healthRate.calculateEmployeeAmount(monthlySalary);
        BigDecimal healthEmployerAmount = healthRate.calculateEmployerAmount(monthlySalary);

        BigDecimal employeeAmount = truncate(healthEmployeeAmount.multiply(longTermRate.getEmployeeRate()));
        BigDecimal employerAmount = truncate(healthEmployerAmount.multiply(longTermRate.getEmployerRate()));

        return DeductionAmount.of(DeductionType.LONG_TERM_CARE_INSURANCE, employeeAmount, employerAmount);
    }

    /**
     * 소득세 계산
     */
    private DeductionAmount calculateIncomeTax(BigDecimal taxableSalary, int dependentsCnt, String year) {
        // 원 → 천원 변환
        int salaryInThousand = taxableSalary
                .divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN)
                .intValue();

        BigDecimal incomeTax = incomeTaxTableRepository
                .findByYearAndSalaryAndDependents(year, salaryInThousand, dependentsCnt)
                .map(IncomeTaxTable::getTaxAmount)
                .orElse(BigDecimal.ZERO);

        return DeductionAmount.taxOf(DeductionType.INCOME_TAX, truncate(incomeTax));
    }

    /**
     * 지방소득세 계산 (소득세 × 10%)
     */
    private DeductionAmount calculateLocalIncomeTax(BigDecimal incomeTax) {
        BigDecimal localTax = truncate(incomeTax.multiply(LOCAL_TAX_RATE));
        return DeductionAmount.taxOf(DeductionType.LOCAL_INCOME_TAX, localTax);
    }

    /**
     * 회사가 체크한 사회보험 목록 조회
     */
    private Set<SocialInsuranceType> getEnrolledInsurances(Long storeUserId) {
        List<WorkerSocialInsurance> insurances = workerSocialInsuranceRepository
                .findAllByStoreUserId(storeUserId);

        return insurances.stream()
                .filter(WorkerSocialInsurance::isEnrolled)
                .map(WorkerSocialInsurance::getInsuranceType)
                .collect(Collectors.toSet());
    }

    /**
     * DeductionType → SocialInsuranceType 변환
     */
    private SocialInsuranceType toSocialInsuranceType(DeductionType type) {
        return switch (type) {
            case NATIONAL_PENSION -> SocialInsuranceType.NATIONAL_PENSION;
            case HEALTH_INSURANCE -> SocialInsuranceType.HEALTH_INSURANCE;
            case LONG_TERM_CARE_INSURANCE -> SocialInsuranceType.LONG_TERM_CARE_INSURANCE;
            case EMPLOYMENT_INSURANCE -> SocialInsuranceType.EMPLOYMENT_INSURANCE;
            default -> null;
        };
    }

    /**
     * 10원 미만 절사
     */
    private BigDecimal truncate(BigDecimal amount) {
        return amount
                .divide(BigDecimal.valueOf(TRUNCATE_UNIT), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(TRUNCATE_UNIT));
    }
}
