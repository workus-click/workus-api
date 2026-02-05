package com.workus.workus.payroll.deduction.domain.service;

import com.workus.workus.payroll.deduction.domain.model.IncomeTaxCalculationResult;
import com.workus.workus.payroll.deduction.domain.model.IncomeTaxTable;
import com.workus.workus.payroll.deduction.domain.repository.IncomeTaxTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("IncomeTaxCalculationService 테스트")
class IncomeTaxCalculationServiceTest {

    @Mock
    private IncomeTaxTableRepository incomeTaxTableRepository;

    @InjectMocks
    private IncomeTaxCalculationService calculationService;

    private static final String YEAR = "2025";

    @Nested
    @DisplayName("calculate 테스트")
    class CalculateTest {

        @Test
        @DisplayName("간이세액표에서 소득세를 조회하고 지방소득세를 계산한다")
        void calculatesIncomeTaxAndLocalTax() {
            // given - 소득세 100,000원
            IncomeTaxTable taxTable = mockTaxTable(new BigDecimal("100000"));
            given(incomeTaxTableRepository.findByYearAndSalaryAndDependents(anyString(), anyInt(), anyInt()))
                    .willReturn(Optional.of(taxTable));

            // when
            IncomeTaxCalculationResult result = calculationService.calculate(3000, 1, YEAR);

            // then
            // 소득세: 100,000원
            assertThat(result.incomeTax()).isEqualByComparingTo(new BigDecimal("100000"));
            // 지방소득세: 100,000 × 10% = 10,000원
            assertThat(result.localIncomeTax()).isEqualByComparingTo(new BigDecimal("10000"));
            // 총액: 110,000원
            assertThat(result.totalTax()).isEqualByComparingTo(new BigDecimal("110000"));
        }

        @Test
        @DisplayName("10원 미만 절사 처리한다")
        void truncatesUnder10Won() {
            // given - 소득세 123,456원
            IncomeTaxTable taxTable = mockTaxTable(new BigDecimal("123456"));
            given(incomeTaxTableRepository.findByYearAndSalaryAndDependents(anyString(), anyInt(), anyInt()))
                    .willReturn(Optional.of(taxTable));

            // when
            IncomeTaxCalculationResult result = calculationService.calculate(3000, 1, YEAR);

            // then
            // 소득세: 123,456 → 123,450 (10원 미만 절사)
            assertThat(result.incomeTax()).isEqualByComparingTo(new BigDecimal("123450"));
            // 지방소득세: 123,456 × 10% = 12,345.6 → 12,340 (10원 미만 절사)
            assertThat(result.localIncomeTax()).isEqualByComparingTo(new BigDecimal("12340"));
        }

        @Test
        @DisplayName("간이세액표에 해당 구간이 없으면 0원을 반환한다")
        void returnsZeroWhenNotFound() {
            // given
            given(incomeTaxTableRepository.findByYearAndSalaryAndDependents(anyString(), anyInt(), anyInt()))
                    .willReturn(Optional.empty());

            // when
            IncomeTaxCalculationResult result = calculationService.calculate(500, 1, YEAR);

            // then
            assertThat(result.incomeTax()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.localIncomeTax()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.totalTax()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("BigDecimal 월급여로 계산 테스트")
    class CalculateWithBigDecimalTest {

        @Test
        @DisplayName("원 단위 월급여를 천원 단위로 변환하여 계산한다")
        void convertsSalaryToThousand() {
            // given - 소득세 50,000원
            IncomeTaxTable taxTable = mockTaxTable(new BigDecimal("50000"));
            given(incomeTaxTableRepository.findByYearAndSalaryAndDependents(YEAR, 3000, 2))
                    .willReturn(Optional.of(taxTable));

            // when - 3,000,500원 (천원 단위 변환 시 3000)
            IncomeTaxCalculationResult result = calculationService.calculate(
                    new BigDecimal("3000500"), 2, YEAR);

            // then
            assertThat(result.incomeTax()).isEqualByComparingTo(new BigDecimal("50000"));
            assertThat(result.localIncomeTax()).isEqualByComparingTo(new BigDecimal("5000"));
        }
    }

    @Nested
    @DisplayName("개별 조회 메서드 테스트")
    class IndividualMethodsTest {

        @Test
        @DisplayName("소득세만 조회할 수 있다")
        void getIncomeTaxOnly() {
            // given
            IncomeTaxTable taxTable = mockTaxTable(new BigDecimal("80000"));
            given(incomeTaxTableRepository.findByYearAndSalaryAndDependents(anyString(), anyInt(), anyInt()))
                    .willReturn(Optional.of(taxTable));

            // when
            BigDecimal incomeTax = calculationService.getIncomeTax(2500, 1, YEAR);

            // then
            assertThat(incomeTax).isEqualByComparingTo(new BigDecimal("80000"));
        }

        @Test
        @DisplayName("지방소득세만 조회할 수 있다")
        void getLocalIncomeTaxOnly() {
            // given
            IncomeTaxTable taxTable = mockTaxTable(new BigDecimal("80000"));
            given(incomeTaxTableRepository.findByYearAndSalaryAndDependents(anyString(), anyInt(), anyInt()))
                    .willReturn(Optional.of(taxTable));

            // when
            BigDecimal localTax = calculationService.getLocalIncomeTax(2500, 1, YEAR);

            // then
            // 80,000 × 10% = 8,000
            assertThat(localTax).isEqualByComparingTo(new BigDecimal("8000"));
        }
    }

    private IncomeTaxTable mockTaxTable(BigDecimal taxAmount) {
        IncomeTaxTable taxTable = mock(IncomeTaxTable.class);
        given(taxTable.getTaxAmount()).willReturn(taxAmount);
        return taxTable;
    }
}
