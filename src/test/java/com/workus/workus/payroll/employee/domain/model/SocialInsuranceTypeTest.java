package com.workus.workus.payroll.employee.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SocialInsuranceType 테스트")
class SocialInsuranceTypeTest {

    @Nested
    @DisplayName("국민연금 (NATIONAL_PENSION)")
    class NationalPensionTest {

        @Test
        @DisplayName("18세 이상 60세 미만은 적용 대상이다")
        void isEligible_ageInRange_returnsTrue() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isEligible(18, 12)).isTrue();
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isEligible(30, 12)).isTrue();
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isEligible(59, 12)).isTrue();
        }

        @Test
        @DisplayName("18세 미만은 적용 대상이 아니다")
        void isEligible_under18_returnsFalse() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isEligible(17, 12)).isFalse();
        }

        @Test
        @DisplayName("60세 이상은 적용 대상이 아니다")
        void isEligible_60OrOver_returnsFalse() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isEligible(60, 12)).isFalse();
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isEligible(65, 12)).isFalse();
        }

        @Test
        @DisplayName("60세 이상 신규 입사자는 비적용 대상이다")
        void isExempt_60OrOver_returnsTrue() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isExempt(60, 12, 40, Nationality.KR)).isTrue();
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isExempt(65, 12, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("1개월 미만 계약은 비적용 대상이다")
        void isExempt_contractUnder1Month_returnsTrue() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isExempt(30, 0, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("정상 조건의 내국인은 적용 대상이다")
        void isApplicable_normalKorean_returnsTrue() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isApplicable(30, 12, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("내국인 기준 오버로드 메서드 테스트")
        void isApplicable_defaultKorean_returnsTrue() {
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isApplicable(30, 12, 40)).isTrue();
            assertThat(SocialInsuranceType.NATIONAL_PENSION.isExempt(30, 12, 40)).isFalse();
        }
    }

    @Nested
    @DisplayName("건강보험 (HEALTH_INSURANCE)")
    class HealthInsuranceTest {

        @Test
        @DisplayName("모든 상용근로자는 적용 대상이다")
        void isEligible_allWorkers_returnsTrue() {
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isEligible(20, 12)).isTrue();
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isEligible(65, 12)).isTrue();
        }

        @Test
        @DisplayName("1개월 미만 계약은 비적용 대상이다")
        void isExempt_contractUnder1Month_returnsTrue() {
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isExempt(30, 0, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("정상 조건의 내국인은 적용 대상이다")
        void isApplicable_normalKorean_returnsTrue() {
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isApplicable(30, 12, 40, Nationality.KR)).isTrue();
        }
    }

    @Nested
    @DisplayName("장기요양보험 (LONG_TERM_CARE_INSURANCE)")
    class LongTermCareInsuranceTest {

        @Test
        @DisplayName("모든 상용근로자는 적용 대상이다")
        void isEligible_allWorkers_returnsTrue() {
            assertThat(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.isEligible(20, 12)).isTrue();
        }

        @Test
        @DisplayName("1개월 미만 계약은 비적용 대상이다")
        void isExempt_contractUnder1Month_returnsTrue() {
            assertThat(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.isExempt(30, 0, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("정상 조건의 내국인은 적용 대상이다")
        void isApplicable_normalKorean_returnsTrue() {
            assertThat(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.isApplicable(30, 12, 40, Nationality.KR)).isTrue();
        }
    }

    @Nested
    @DisplayName("고용보험 (EMPLOYMENT_INSURANCE)")
    class EmploymentInsuranceTest {

        @Test
        @DisplayName("1개월 이상 계약자는 적용 대상이다")
        void isEligible_contractOver1Month_returnsTrue() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isEligible(30, 1)).isTrue();
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isEligible(30, 12)).isTrue();
        }

        @Test
        @DisplayName("1개월 미만 계약자는 적용 대상이 아니다")
        void isEligible_contractUnder1Month_returnsFalse() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isEligible(30, 0)).isFalse();
        }

        @Test
        @DisplayName("1개월 미만 계약은 비적용 대상이다")
        void isExempt_contractUnder1Month_returnsTrue() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isExempt(30, 0, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("주 15시간 미만 근로자는 비적용 대상이다")
        void isExempt_weeklyHoursUnder15_returnsTrue() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isExempt(30, 12, 14.9, Nationality.KR)).isTrue();
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isExempt(30, 12, 10, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("주 15시간 이상 근로자는 비적용 대상이 아니다")
        void isExempt_weeklyHours15OrMore_returnsFalse() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isExempt(30, 12, 15, Nationality.KR)).isFalse();
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isExempt(30, 12, 40, Nationality.KR)).isFalse();
        }

        @Test
        @DisplayName("정상 조건의 내국인은 적용 대상이다")
        void isApplicable_normalKorean_returnsTrue() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isApplicable(30, 12, 40, Nationality.KR)).isTrue();
        }

        @Test
        @DisplayName("주 15시간 미만이면 적용 대상이 아니다")
        void isApplicable_weeklyHoursUnder15_returnsFalse() {
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isApplicable(30, 12, 10, Nationality.KR)).isFalse();
        }
    }

    @Nested
    @DisplayName("산재보험 (INDUSTRIAL_ACCIDENT_INSURANCE)")
    class IndustrialAccidentInsuranceTest {

        @Test
        @DisplayName("모든 근로자는 적용 대상이다")
        void isEligible_allWorkers_returnsTrue() {
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isEligible(20, 0)).isTrue();
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isEligible(70, 12)).isTrue();
        }

        @Test
        @DisplayName("비적용 대상이 없다")
        void isExempt_noExemption_returnsFalse() {
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isExempt(20, 0, 5, Nationality.KR)).isFalse();
        }

        @Test
        @DisplayName("모든 조건에서 적용 대상이다")
        void isApplicable_always_returnsTrue() {
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isApplicable(20, 0, 5, Nationality.KR)).isTrue();
        }
    }

    @Nested
    @DisplayName("isEligibleByAge 테스트")
    class IsEligibleByAgeTest {

        @ParameterizedTest
        @CsvSource({
            "NATIONAL_PENSION, 17, false",
            "NATIONAL_PENSION, 18, true",
            "NATIONAL_PENSION, 59, true",
            "NATIONAL_PENSION, 60, false",
            "HEALTH_INSURANCE, 20, true",
            "HEALTH_INSURANCE, 70, true",
            "EMPLOYMENT_INSURANCE, 30, true",
            "INDUSTRIAL_ACCIDENT_INSURANCE, 30, true"
        })
        @DisplayName("나이만으로 적용 대상 여부를 판단한다")
        void isEligibleByAge(SocialInsuranceType type, int age, boolean expected) {
            assertThat(type.isEligibleByAge(age)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Nationality 테스트")
    class NationalityTest {

        @Test
        @DisplayName("KR은 내국인이다")
        void kr_isDomestic() {
            assertThat(Nationality.KR.isDomestic()).isTrue();
            assertThat(Nationality.KR.isForeigner()).isFalse();
        }

        @Test
        @DisplayName("외국 국적은 외국인이다")
        void foreignCountry_isForeigner() {
            assertThat(Nationality.CN.isForeigner()).isTrue();
            assertThat(Nationality.VN.isForeigner()).isTrue();
            assertThat(Nationality.US.isForeigner()).isTrue();
            assertThat(Nationality.CN.isDomestic()).isFalse();
        }

        @Test
        @DisplayName("내국인은 국민연금 협정국으로 간주된다")
        void domestic_isPensionTreatyCountry() {
            assertThat(Nationality.KR.isPensionTreatyCountry()).isTrue();
        }

        @Test
        @DisplayName("협정국 외국인은 국민연금 협정국이다")
        void treatyCountry_isPensionTreatyCountry() {
            assertThat(Nationality.US.isPensionTreatyCountry()).isTrue();
            assertThat(Nationality.DE.isPensionTreatyCountry()).isTrue();
            assertThat(Nationality.JP.isPensionTreatyCountry()).isTrue();
        }

        @Test
        @DisplayName("비협정국 외국인은 국민연금 협정국이 아니다")
        void nonTreatyCountry_isNotPensionTreatyCountry() {
            assertThat(Nationality.CN.isPensionTreatyCountry()).isFalse();
            assertThat(Nationality.VN.isPensionTreatyCountry()).isFalse();
            assertThat(Nationality.PH.isPensionTreatyCountry()).isFalse();
        }

        @Test
        @DisplayName("내국인은 건강보험 적용 대상이다")
        void domestic_isHealthInsuranceEligible() {
            assertThat(Nationality.KR.isHealthInsuranceEligible()).isTrue();
        }

        @Test
        @DisplayName("ISO 코드가 정확히 반환된다")
        void getAlpha2Code_returnsCorrectCode() {
            assertThat(Nationality.KR.getAlpha2Code()).isEqualTo("KR");
            assertThat(Nationality.US.getAlpha2Code()).isEqualTo("US");
            assertThat(Nationality.VN.getAlpha2Code()).isEqualTo("VN");
        }

        @Test
        @DisplayName("문자열 코드로 Nationality를 조회할 수 있다")
        void fromCode_returnsNationality() {
            assertThat(Nationality.fromCode("KR")).isEqualTo(Nationality.KR);
            assertThat(Nationality.fromCode("kr")).isEqualTo(Nationality.KR);
            assertThat(Nationality.fromCode("US")).isEqualTo(Nationality.US);
        }

        @Test
        @DisplayName("지원하지 않는 코드는 예외를 던진다")
        void fromCode_invalidCode_throwsException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Nationality.fromCode("XX")
            );
        }

        @Test
        @DisplayName("null 또는 빈 코드는 예외를 던진다")
        void fromCode_nullOrEmpty_throwsException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Nationality.fromCode(null)
            );
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Nationality.fromCode("")
            );
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("25세 정규직 내국인은 모든 보험 적용 대상이다")
        void normalFullTimeKorean_allInsuranceApplicable() {
            int age = 25;
            int contractMonths = 12;
            double weeklyHours = 40;
            Nationality nationality = Nationality.KR;

            for (SocialInsuranceType type : SocialInsuranceType.values()) {
                assertThat(type.isApplicable(age, contractMonths, weeklyHours, nationality))
                        .as("%s 적용 여부", type.getDescription())
                        .isTrue();
            }
        }

        @Test
        @DisplayName("65세 신규 입사자는 국민연금만 비적용이다")
        void seniorNewHire_onlyPensionExempt() {
            int age = 65;
            int contractMonths = 12;
            double weeklyHours = 40;
            Nationality nationality = Nationality.KR;

            assertThat(SocialInsuranceType.NATIONAL_PENSION.isApplicable(age, contractMonths, weeklyHours, nationality)).isFalse();
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
            assertThat(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
        }

        @Test
        @DisplayName("주 10시간 단시간 근로자는 고용보험 비적용이다")
        void partTimeWorker_employmentInsuranceExempt() {
            int age = 30;
            int contractMonths = 12;
            double weeklyHours = 10;
            Nationality nationality = Nationality.KR;

            assertThat(SocialInsuranceType.NATIONAL_PENSION.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
            assertThat(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isFalse();
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
        }

        @Test
        @DisplayName("일용직(1개월 미만)은 산재보험만 적용된다")
        void dailyWorker_onlyIndustrialAccidentApplicable() {
            int age = 30;
            int contractMonths = 0;
            double weeklyHours = 40;
            Nationality nationality = Nationality.KR;

            assertThat(SocialInsuranceType.NATIONAL_PENSION.isApplicable(age, contractMonths, weeklyHours, nationality)).isFalse();
            assertThat(SocialInsuranceType.HEALTH_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isFalse();
            assertThat(SocialInsuranceType.LONG_TERM_CARE_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isFalse();
            assertThat(SocialInsuranceType.EMPLOYMENT_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isFalse();
            assertThat(SocialInsuranceType.INDUSTRIAL_ACCIDENT_INSURANCE.isApplicable(age, contractMonths, weeklyHours, nationality)).isTrue();
        }
    }
}
