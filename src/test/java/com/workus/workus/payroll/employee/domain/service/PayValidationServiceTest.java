package com.workus.workus.payroll.employee.domain.service;

import com.workus.workus.payroll.employee.domain.model.PayType;
import com.workus.workus.payroll.employee.presentation.dto.PayValidationRequest;
import com.workus.workus.payroll.employee.presentation.dto.PayValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PayValidationService 테스트")
class PayValidationServiceTest {

    private PayValidationService payValidationService;

    private static final BigDecimal MINIMUM_HOURLY_RATE = BigDecimal.valueOf(10320);

    @BeforeEach
    void setUp() {
        payValidationService = new PayValidationService();
    }

    @Nested
    @DisplayName("시급제 유효성 검사")
    class HourlyPayValidation {

        @Test
        @DisplayName("최저시급 이상이면 유효하다")
        void validHourlyRate() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(12000),  // 시급 12,000원
                    null,
                    8,  // 일 8시간
                    5   // 주 5일
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.message()).isEqualTo("유효한 급여입니다.");
            assertThat(response.payType()).isEqualTo(PayType.HOURLY);
            assertThat(response.hourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(12000));
            assertThat(response.monthlySalary()).isEqualByComparingTo(BigDecimal.valueOf(1920000)); // 12000 * 8 * 5 * 4
            assertThat(response.monthlyWorkHours()).isEqualTo(160);
        }

        @Test
        @DisplayName("정확히 최저시급이면 유효하다")
        void exactMinimumHourlyRate() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    MINIMUM_HOURLY_RATE,
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.hourlyRate()).isEqualByComparingTo(MINIMUM_HOURLY_RATE);
        }

        @Test
        @DisplayName("최저시급 미만이면 유효하지 않다")
        void belowMinimumHourlyRate() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(9000),  // 최저시급 미만
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isFalse();
            assertThat(response.message()).contains("최저시급");
            assertThat(response.message()).contains("9000원");
            assertThat(response.hourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(9000));
            assertThat(response.monthlySalary()).isEqualByComparingTo(BigDecimal.valueOf(1440000)); // 9000 * 8 * 5 * 4
        }

        @Test
        @DisplayName("시급이 null이면 유효하지 않다")
        void nullHourlyRate() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    null,  // 시급 없음
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isFalse();
            assertThat(response.message()).contains("시급제의 경우 시급을 입력해야 합니다");
        }

        @Test
        @DisplayName("다양한 근무시간으로 월급이 정확히 계산된다")
        void calculateMonthlySalaryWithDifferentWorkHours() {
            // given: 시급 15000원, 일 6시간, 주 4일
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(15000),
                    null,
                    6,  // 일 6시간
                    4   // 주 4일
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.monthlyWorkHours()).isEqualTo(96); // 6 * 4 * 4
            assertThat(response.monthlySalary()).isEqualByComparingTo(BigDecimal.valueOf(1440000)); // 15000 * 96
        }
    }

    @Nested
    @DisplayName("월급제 유효성 검사")
    class MonthlyPayValidation {

        @Test
        @DisplayName("환산 시급이 최저시급 이상이면 유효하다")
        void validMonthlySalary() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.MONTHLY,
                    null,
                    BigDecimal.valueOf(2500000),  // 월급 250만원
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.message()).isEqualTo("유효한 급여입니다.");
            assertThat(response.payType()).isEqualTo(PayType.MONTHLY);
            assertThat(response.monthlySalary()).isEqualByComparingTo(BigDecimal.valueOf(2500000));
            assertThat(response.hourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(15625)); // 2500000 / 160
            assertThat(response.monthlyWorkHours()).isEqualTo(160);
        }

        @Test
        @DisplayName("환산 시급이 최저시급 미만이면 유효하지 않다")
        void belowMinimumHourlyRateWhenConverted() {
            // given: 월급 150만원, 160시간 = 시급 9375원 (최저시급 미만)
            PayValidationRequest request = new PayValidationRequest(
                    PayType.MONTHLY,
                    null,
                    BigDecimal.valueOf(1500000),
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isFalse();
            assertThat(response.message()).contains("환산 시급이 최저시급");
            assertThat(response.hourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(9375)); // 1500000 / 160 = 9375
        }

        @Test
        @DisplayName("월급이 null이면 유효하지 않다")
        void nullMonthlySalary() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.MONTHLY,
                    null,
                    null,  // 월급 없음
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isFalse();
            assertThat(response.message()).contains("월급제의 경우 월급을 입력해야 합니다");
        }

        @Test
        @DisplayName("정확히 최저시급 경계값이면 유효하다")
        void exactMinimumWhenConverted() {
            // given: 최저시급 * 160시간 = 1,651,200원
            BigDecimal exactMinimumMonthlySalary = MINIMUM_HOURLY_RATE.multiply(BigDecimal.valueOf(160));
            PayValidationRequest request = new PayValidationRequest(
                    PayType.MONTHLY,
                    null,
                    exactMinimumMonthlySalary,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.hourlyRate()).isGreaterThanOrEqualTo(MINIMUM_HOURLY_RATE);
        }

        @Test
        @DisplayName("다양한 근무시간으로 시급이 정확히 계산된다")
        void calculateHourlyRateWithDifferentWorkHours() {
            // given: 월급 200만원, 일 6시간, 주 4일 = 96시간 = 시급 20833.33원
            PayValidationRequest request = new PayValidationRequest(
                    PayType.MONTHLY,
                    null,
                    BigDecimal.valueOf(2000000),
                    6,
                    4
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.monthlyWorkHours()).isEqualTo(96);
            // 2000000 / 96 = 20833.33... (FLOOR) = 20833.33
            assertThat(response.hourlyRate()).isEqualByComparingTo(new BigDecimal("20833.33"));
        }
    }

    @Nested
    @DisplayName("공통 검증")
    class CommonValidation {

        @Test
        @DisplayName("최저시급 정보가 응답에 포함된다")
        void minimumHourlyRateIncludedInResponse() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(12000),
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.minimumHourlyRate()).isEqualByComparingTo(MINIMUM_HOURLY_RATE);
        }

        @Test
        @DisplayName("근무시간 정보가 응답에 포함된다")
        void workHoursIncludedInResponse() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(12000),
                    null,
                    7,
                    6
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.workHoursPerDay()).isEqualTo(7);
            assertThat(response.workDaysPerWeek()).isEqualTo(6);
            assertThat(response.monthlyWorkHours()).isEqualTo(168); // 7 * 6 * 4
        }

        @Test
        @DisplayName("지원하지 않는 급여 형태는 예외를 던진다")
        void unsupportedPayType() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.DAILY,  // 미지원 타입
                    BigDecimal.valueOf(80000),
                    null,
                    8,
                    5
            );

            // when & then
            assertThatThrownBy(() -> payValidationService.validate(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("지원하지 않는 급여 형태");
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("최저시급보다 1원 낮으면 유효하지 않다")
        void oneBelowMinimum() {
            // given
            BigDecimal justBelowMinimum = MINIMUM_HOURLY_RATE.subtract(BigDecimal.ONE);
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    justBelowMinimum,
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isFalse();
        }

        @Test
        @DisplayName("최저시급보다 1원 높으면 유효하다")
        void oneAboveMinimum() {
            // given
            BigDecimal justAboveMinimum = MINIMUM_HOURLY_RATE.add(BigDecimal.ONE);
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    justAboveMinimum,
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
        }

        @Test
        @DisplayName("매우 높은 시급도 유효하다")
        void veryHighHourlyRate() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(1000000),  // 시급 100만원
                    null,
                    8,
                    5
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.monthlySalary()).isEqualByComparingTo(BigDecimal.valueOf(160000000)); // 1억 6천만원
        }

        @Test
        @DisplayName("최소 근무시간(일1시간, 주1일)으로도 계산된다")
        void minimumWorkHours() {
            // given
            PayValidationRequest request = new PayValidationRequest(
                    PayType.HOURLY,
                    BigDecimal.valueOf(15000),
                    null,
                    1,  // 일 1시간
                    1   // 주 1일
            );

            // when
            PayValidationResponse response = payValidationService.validate(request);

            // then
            assertThat(response.valid()).isTrue();
            assertThat(response.monthlyWorkHours()).isEqualTo(4); // 1 * 1 * 4
            assertThat(response.monthlySalary()).isEqualByComparingTo(BigDecimal.valueOf(60000)); // 15000 * 4
        }
    }
}
