package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.employee.domain.model.WorkerSocialInsurance;
import com.workus.workus.payroll.employee.domain.model.Nationality;
import com.workus.workus.payroll.employee.domain.model.SocialInsuranceType;
import com.workus.workus.payroll.employee.domain.repository.WorkerSocialInsuranceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SocialInsuranceEligibilityService 테스트")
class SocialInsuranceEligibilityServiceTest {

    @Mock
    private WorkerSocialInsuranceRepository workerSocialInsuranceRepository;

    @InjectMocks
    private SocialInsuranceEligibilityService eligibilityService;

    private static final Long STORE_USER_ID = 1L;

    @Nested
    @DisplayName("isEligible 테스트")
    class IsEligibleTest {

        @Test
        @DisplayName("회사 미체크 시 적용 대상이 아니다")
        void notEnrolledByCompany_returnsFalse() {
            // given
            given(workerSocialInsuranceRepository.findByStoreUserIdAndInsuranceType(
                    eq(STORE_USER_ID), any(SocialInsuranceType.class)))
                    .willReturn(Optional.empty());

            // when
            boolean result = eligibilityService.isEligible(
                    STORE_USER_ID,
                    SocialInsuranceType.NATIONAL_PENSION,
                    30, 12, 40, Nationality.KR);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("회사 체크 + 근로자 조건 충족 시 적용 대상이다")
        void enrolledAndWorkerEligible_returnsTrue() {
            // given
            WorkerSocialInsurance insurance = mockInsurance(SocialInsuranceType.NATIONAL_PENSION, true);
            given(workerSocialInsuranceRepository.findByStoreUserIdAndInsuranceType(
                    STORE_USER_ID, SocialInsuranceType.NATIONAL_PENSION))
                    .willReturn(Optional.of(insurance));

            // when
            boolean result = eligibilityService.isEligible(
                    STORE_USER_ID,
                    SocialInsuranceType.NATIONAL_PENSION,
                    30, 12, 40, Nationality.KR);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("회사 체크 + 근로자 조건 미충족 시 적용 대상이 아니다")
        void enrolledButWorkerNotEligible_returnsFalse() {
            // given
            WorkerSocialInsurance insurance = mockInsurance(SocialInsuranceType.NATIONAL_PENSION, true);
            given(workerSocialInsuranceRepository.findByStoreUserIdAndInsuranceType(
                    STORE_USER_ID, SocialInsuranceType.NATIONAL_PENSION))
                    .willReturn(Optional.of(insurance));

            // when - 65세는 국민연금 적용 대상이 아님
            boolean result = eligibilityService.isEligible(
                    STORE_USER_ID,
                    SocialInsuranceType.NATIONAL_PENSION,
                    65, 12, 40, Nationality.KR);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("checkAllEligibility 테스트")
    class CheckAllEligibilityTest {

        @Test
        @DisplayName("전체 보험 적용 여부를 Map으로 반환한다")
        void returnsAllInsuranceEligibility() {
            // given
            List<WorkerSocialInsurance> insurances = List.of(
                    mockInsurance(SocialInsuranceType.NATIONAL_PENSION, true),
                    mockInsurance(SocialInsuranceType.HEALTH_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.LONG_TERM_CARE_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.EMPLOYMENT_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE, true)
            );
            given(workerSocialInsuranceRepository.findAllByStoreUserId(STORE_USER_ID))
                    .willReturn(insurances);

            // when - 25세 정규직 내국인
            Map<SocialInsuranceType, Boolean> result = eligibilityService.checkAllEligibility(
                    STORE_USER_ID, 25, 12, 40, Nationality.KR);

            // then - 모든 보험 적용
            assertThat(result).containsEntry(SocialInsuranceType.NATIONAL_PENSION, true);
            assertThat(result).containsEntry(SocialInsuranceType.HEALTH_INSURANCE, true);
            assertThat(result).containsEntry(SocialInsuranceType.LONG_TERM_CARE_INSURANCE, true);
            assertThat(result).containsEntry(SocialInsuranceType.EMPLOYMENT_INSURANCE, true);
            assertThat(result).containsEntry(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE, true);
        }

        @Test
        @DisplayName("65세 근로자는 국민연금만 비적용이다")
        void seniorWorker_pensionNotEligible() {
            // given
            List<WorkerSocialInsurance> insurances = List.of(
                    mockInsurance(SocialInsuranceType.NATIONAL_PENSION, true),
                    mockInsurance(SocialInsuranceType.HEALTH_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.LONG_TERM_CARE_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.EMPLOYMENT_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE, true)
            );
            given(workerSocialInsuranceRepository.findAllByStoreUserId(STORE_USER_ID))
                    .willReturn(insurances);

            // when - 65세 정규직 내국인
            Map<SocialInsuranceType, Boolean> result = eligibilityService.checkAllEligibility(
                    STORE_USER_ID, 65, 12, 40, Nationality.KR);

            // then
            assertThat(result).containsEntry(SocialInsuranceType.NATIONAL_PENSION, false);
            assertThat(result).containsEntry(SocialInsuranceType.HEALTH_INSURANCE, true);
            assertThat(result).containsEntry(SocialInsuranceType.EMPLOYMENT_INSURANCE, true);
        }

        @Test
        @DisplayName("주 10시간 단시간 근로자는 고용보험 비적용이다")
        void partTimeWorker_employmentInsuranceNotEligible() {
            // given
            List<WorkerSocialInsurance> insurances = List.of(
                    mockInsurance(SocialInsuranceType.NATIONAL_PENSION, true),
                    mockInsurance(SocialInsuranceType.HEALTH_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.LONG_TERM_CARE_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.EMPLOYMENT_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE, true)
            );
            given(workerSocialInsuranceRepository.findAllByStoreUserId(STORE_USER_ID))
                    .willReturn(insurances);

            // when - 주 10시간 단시간 근로자
            Map<SocialInsuranceType, Boolean> result = eligibilityService.checkAllEligibility(
                    STORE_USER_ID, 30, 12, 10, Nationality.KR);

            // then
            assertThat(result).containsEntry(SocialInsuranceType.NATIONAL_PENSION, true);
            assertThat(result).containsEntry(SocialInsuranceType.EMPLOYMENT_INSURANCE, false);
        }
    }

    @Nested
    @DisplayName("getEligibleInsurances 테스트")
    class GetEligibleInsurancesTest {

        @Test
        @DisplayName("적용 가능한 보험 목록만 반환한다")
        void returnsOnlyEligibleInsurances() {
            // given
            List<WorkerSocialInsurance> insurances = List.of(
                    mockInsurance(SocialInsuranceType.NATIONAL_PENSION, true),
                    mockInsurance(SocialInsuranceType.HEALTH_INSURANCE, false),  // 미체크
                    mockInsurance(SocialInsuranceType.EMPLOYMENT_INSURANCE, true),
                    mockInsurance(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE, true)
            );
            given(workerSocialInsuranceRepository.findAllByStoreUserId(STORE_USER_ID))
                    .willReturn(insurances);

            // when
            Set<SocialInsuranceType> result = eligibilityService.getEligibleInsurances(
                    STORE_USER_ID, 30, 12, 40, Nationality.KR);

            // then
            assertThat(result).contains(
                    SocialInsuranceType.NATIONAL_PENSION,
                    SocialInsuranceType.EMPLOYMENT_INSURANCE,
                    SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE
            );
            assertThat(result).doesNotContain(SocialInsuranceType.HEALTH_INSURANCE);
        }
    }

    @Nested
    @DisplayName("isWorkerEligible 테스트")
    class IsWorkerEligibleTest {

        @Test
        @DisplayName("근로자 조건만으로 적용 여부를 판단한다")
        void checksWorkerConditionOnly() {
            // when & then
            assertThat(eligibilityService.isWorkerEligible(
                    SocialInsuranceType.NATIONAL_PENSION, 30, 12, 40, Nationality.KR))
                    .isTrue();

            assertThat(eligibilityService.isWorkerEligible(
                    SocialInsuranceType.NATIONAL_PENSION, 65, 12, 40, Nationality.KR))
                    .isFalse();

            assertThat(eligibilityService.isWorkerEligible(
                    SocialInsuranceType.EMPLOYMENT_INSURANCE, 30, 12, 10, Nationality.KR))
                    .isFalse();
        }
    }

    private WorkerSocialInsurance mockInsurance(SocialInsuranceType type, boolean enrolled) {
        WorkerSocialInsurance insurance = mock(WorkerSocialInsurance.class);
        given(insurance.getInsuranceType()).willReturn(type);
        given(insurance.isEnrolled()).willReturn(enrolled);
        return insurance;
    }
}
