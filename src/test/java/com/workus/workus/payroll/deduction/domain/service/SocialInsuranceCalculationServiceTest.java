package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.employee.domain.model.Nationality;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.deduction.domain.model.SocialInsuranceCalculationResult;
import com.workus.workus.payroll.deduction.domain.model.SocialInsuranceRate;
import com.workus.workus.payroll.deduction.domain.repository.SocialInsuranceRateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SocialInsuranceCalculationService 테스트")
class SocialInsuranceCalculationServiceTest {

    @Mock
    private SocialInsuranceEligibilityService eligibilityService;

    @Mock
    private SocialInsuranceRateRepository socialInsuranceRateRepository;

    @InjectMocks
    private SocialInsuranceCalculationService calculationService;

    private static final Long STORE_USER_ID = 1L;
    private static final String YEAR = "2025";
    private static final BigDecimal MONTHLY_SALARY = new BigDecimal("3000000");

    @Nested
    @DisplayName("calculate 테스트")
    class CalculateTest {

        @Test
        @DisplayName("적용 대상 보험의 보험료를 계산한다")
        void calculatesInsuranceAmounts() {
            // given
            Set<SocialInsuranceType> eligibleInsurances = EnumSet.of(
                    SocialInsuranceType.NATIONAL_PENSION,
                    SocialInsuranceType.HEALTH_INSURANCE,
                    SocialInsuranceType.EMPLOYMENT_INSURANCE
            );

            given(eligibilityService.getEligibleInsurances(
                    eq(STORE_USER_ID), anyInt(), anyInt(), anyDouble(), any(Nationality.class)))
                    .willReturn(eligibleInsurances);

            List<SocialInsuranceRate> rates = List.of(
                    mockRate("NATIONAL_PENSION", "0.045", "0.045", 400000L, 6370000L),
                    mockRate("HEALTH_INSURANCE", "0.03545", "0.03545", null, null),
                    mockRate("EMPLOYMENT_INSURANCE", "0.009", "0.009", null, null)
            );
            given(socialInsuranceRateRepository.findAllByYear(YEAR)).willReturn(rates);

            // when
            SocialInsuranceCalculationResult result = calculationService.calculate(
                    STORE_USER_ID,
                    MONTHLY_SALARY,
                    30, 12, 40,
                    Nationality.KR,
                    YEAR
            );

            // then
            assertThat(result.isApplicable(SocialInsuranceType.NATIONAL_PENSION)).isTrue();
            assertThat(result.isApplicable(SocialInsuranceType.HEALTH_INSURANCE)).isTrue();
            assertThat(result.isApplicable(SocialInsuranceType.EMPLOYMENT_INSURANCE)).isTrue();
            assertThat(result.isApplicable(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE)).isFalse();

            // 국민연금: 3,000,000 * 0.045 = 135,000
            assertThat(result.getEmployeeAmount(SocialInsuranceType.NATIONAL_PENSION))
                    .isEqualByComparingTo(new BigDecimal("135000"));

            // 건강보험: 3,000,000 * 0.03545 = 106,350
            assertThat(result.getEmployeeAmount(SocialInsuranceType.HEALTH_INSURANCE))
                    .isEqualByComparingTo(new BigDecimal("106350"));

            // 고용보험: 3,000,000 * 0.009 = 27,000
            assertThat(result.getEmployeeAmount(SocialInsuranceType.EMPLOYMENT_INSURANCE))
                    .isEqualByComparingTo(new BigDecimal("27000"));
        }

        @Test
        @DisplayName("적용 대상이 아닌 보험은 계산하지 않는다")
        void doesNotCalculateForNotEligible() {
            // given
            Set<SocialInsuranceType> eligibleInsurances = EnumSet.of(
                    SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE
            );

            given(eligibilityService.getEligibleInsurances(
                    eq(STORE_USER_ID), anyInt(), anyInt(), anyDouble(), any(Nationality.class)))
                    .willReturn(eligibleInsurances);

            given(socialInsuranceRateRepository.findAllByYear(YEAR)).willReturn(List.of());

            // when
            SocialInsuranceCalculationResult result = calculationService.calculate(
                    STORE_USER_ID,
                    MONTHLY_SALARY,
                    30, 12, 40,
                    Nationality.KR,
                    YEAR
            );

            // then
            assertThat(result.isApplicable(SocialInsuranceType.NATIONAL_PENSION)).isFalse();
            assertThat(result.isApplicable(SocialInsuranceType.HEALTH_INSURANCE)).isFalse();
            assertThat(result.getEmployeeAmount(SocialInsuranceType.NATIONAL_PENSION))
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("총 부담액을 계산한다")
        void calculatesTotalAmounts() {
            // given
            Set<SocialInsuranceType> eligibleInsurances = EnumSet.of(
                    SocialInsuranceType.NATIONAL_PENSION,
                    SocialInsuranceType.HEALTH_INSURANCE
            );

            given(eligibilityService.getEligibleInsurances(
                    eq(STORE_USER_ID), anyInt(), anyInt(), anyDouble(), any(Nationality.class)))
                    .willReturn(eligibleInsurances);

            List<SocialInsuranceRate> rates = List.of(
                    mockRate("NATIONAL_PENSION", "0.045", "0.045", null, null),
                    mockRate("HEALTH_INSURANCE", "0.03545", "0.03545", null, null)
            );
            given(socialInsuranceRateRepository.findAllByYear(YEAR)).willReturn(rates);

            // when
            SocialInsuranceCalculationResult result = calculationService.calculate(
                    STORE_USER_ID,
                    MONTHLY_SALARY,
                    30, 12, 40,
                    Nationality.KR,
                    YEAR
            );

            // then
            // 직원 부담: 135,000 + 106,350 = 241,350
            assertThat(result.totalEmployeeAmount())
                    .isEqualByComparingTo(new BigDecimal("241350"));
            // 회사 부담: 135,000 + 106,350 = 241,350
            assertThat(result.totalEmployerAmount())
                    .isEqualByComparingTo(new BigDecimal("241350"));
        }

        @Test
        @DisplayName("기준소득 상한을 적용한다")
        void appliesMaxBaseAmount() {
            // given
            Set<SocialInsuranceType> eligibleInsurances = EnumSet.of(
                    SocialInsuranceType.NATIONAL_PENSION
            );

            given(eligibilityService.getEligibleInsurances(
                    eq(STORE_USER_ID), anyInt(), anyInt(), anyDouble(), any(Nationality.class)))
                    .willReturn(eligibleInsurances);

            // 상한 637만원
            List<SocialInsuranceRate> rates = List.of(
                    mockRate("NATIONAL_PENSION", "0.045", "0.045", 400000L, 6370000L)
            );
            given(socialInsuranceRateRepository.findAllByYear(YEAR)).willReturn(rates);

            // when - 월급 1000만원 (상한 초과)
            SocialInsuranceCalculationResult result = calculationService.calculate(
                    STORE_USER_ID,
                    new BigDecimal("10000000"),
                    30, 12, 40,
                    Nationality.KR,
                    YEAR
            );

            // then - 상한 637만원 기준 계산: 6,370,000 * 0.045 = 286,650
            assertThat(result.getEmployeeAmount(SocialInsuranceType.NATIONAL_PENSION))
                    .isEqualByComparingTo(new BigDecimal("286650"));
        }
    }

    @Nested
    @DisplayName("장기요양보험 계산 테스트")
    class LongTermCareInsuranceTest {

        @Test
        @DisplayName("장기요양보험료는 건강보험료 기준으로 계산한다")
        void calculatesBasedOnHealthInsurance() {
            // given
            Set<SocialInsuranceType> eligibleInsurances = EnumSet.of(
                    SocialInsuranceType.HEALTH_INSURANCE,
                    SocialInsuranceType.LONG_TERM_CARE_INSURANCE
            );

            given(eligibilityService.getEligibleInsurances(
                    eq(STORE_USER_ID), anyInt(), anyInt(), anyDouble(), any(Nationality.class)))
                    .willReturn(eligibleInsurances);

            List<SocialInsuranceRate> rates = List.of(
                    mockRate("HEALTH_INSURANCE", "0.03545", "0.03545", null, null),
                    mockRate("LONG_TERM_CARE_INSURANCE", "0.1295", "0.1295", null, null)
            );
            given(socialInsuranceRateRepository.findAllByYear(YEAR)).willReturn(rates);

            // when
            SocialInsuranceCalculationResult result = calculationService.calculate(
                    STORE_USER_ID,
                    MONTHLY_SALARY,
                    30, 12, 40,
                    Nationality.KR,
                    YEAR
            );

            // then
            // 건강보험료: 3,000,000 * 0.03545 = 106,350
            // 장기요양보험료: 106,350 * 0.1295 = 13,772 (소수점 버림)
            assertThat(result.getEmployeeAmount(SocialInsuranceType.LONG_TERM_CARE_INSURANCE))
                    .isEqualByComparingTo(new BigDecimal("13772"));
        }
    }

    private SocialInsuranceRate mockRate(String insuranceType, String employeeRate, String employerRate,
                                          Long minBase, Long maxBase) {
        SocialInsuranceRate rate = mock(SocialInsuranceRate.class);
        given(rate.getInsuranceType()).willReturn(insuranceType);
        given(rate.getEmployeeRate()).willReturn(new BigDecimal(employeeRate));
        given(rate.getEmployerRate()).willReturn(new BigDecimal(employerRate));

        // calculateEmployeeAmount, calculateEmployerAmount mock
        given(rate.calculateEmployeeAmount(any(BigDecimal.class))).willAnswer(invocation -> {
            BigDecimal salary = invocation.getArgument(0);
            BigDecimal baseSalary = applyLimit(salary, minBase, maxBase);
            return baseSalary.multiply(new BigDecimal(employeeRate));
        });

        given(rate.calculateEmployerAmount(any(BigDecimal.class))).willAnswer(invocation -> {
            BigDecimal salary = invocation.getArgument(0);
            BigDecimal baseSalary = applyLimit(salary, minBase, maxBase);
            return baseSalary.multiply(new BigDecimal(employerRate));
        });

        return rate;
    }

    private BigDecimal applyLimit(BigDecimal salary, Long minBase, Long maxBase) {
        if (minBase != null && salary.compareTo(BigDecimal.valueOf(minBase)) < 0) {
            return BigDecimal.valueOf(minBase);
        }
        if (maxBase != null && salary.compareTo(BigDecimal.valueOf(maxBase)) > 0) {
            return BigDecimal.valueOf(maxBase);
        }
        return salary;
    }
}
