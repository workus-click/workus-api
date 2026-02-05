package com.workus.workus.payroll.formula.domain.model;

import com.github.f4b6a3.tsid.TsidFactory;
import com.workus.workus.common.component.IdGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PayrollFormulaTest {

    @BeforeAll
    static void initIdGenerator() {
        new IdGenerator(TsidFactory.newInstance256());
    }

    @Nested
    @DisplayName("급여 계산식 생성")
    class PayrollFormulaCreation {
        @Test
        @DisplayName("정상적인 파라미터로 생성할 수 있다")
        void shouldCreate_whenValidParameters() {
            Long storeId = 1L;
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");

            PayrollFormula payrollFormula = PayrollFormula.of(storeId, formula);

            assertThat(payrollFormula).isNotNull();
            assertThat(payrollFormula.getStoreId()).isEqualTo(storeId);
            assertThat(payrollFormula.getFormula()).isEqualTo(formula);
        }
    }

    @Nested
    @DisplayName("계산식 검증")
    class ValidateFormula {
        @Nested
        @DisplayName("유효한 계산식인 경우")
        class GivenValidFormula {
            @Test
            @DisplayName("검증이 통과한다")
            void shouldPassValidation_whenFormulaIsValid() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                assertThatCode(() -> payrollFormula.validateFormula(variableValues))
                        .doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayName("계산식이 null인 경우")
        class GivenNullFormula {
            @Test
            @DisplayName("검증이 실패한다")
            void shouldFailValidation_whenFormulaIsNull() {
                PayrollFormula payrollFormula = PayrollFormula.of(1L, null);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                assertThatThrownBy(() -> payrollFormula.validateFormula(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식이 설정되지 않았습니다");
            }
        }

        @Nested
        @DisplayName("변수가 부족한 경우")
        class GivenMissingVariable {
            @Test
            @DisplayName("계산식에 필요한 변수가 없으면 검증이 실패한다")
            void shouldFailValidation_whenRequiredVariableIsMissing() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();

                assertThatThrownBy(() -> payrollFormula.validateFormula(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }

            @Test
            @DisplayName("여러 변수 중 일부만 제공하면 검증이 실패한다")
            void shouldFailValidation_whenSomeVariablesMissing() {
                Formula formula = Formula.of(PayItemFormulaType.OVERTIME_ALLOWANCE, "BASE_SALARY + OVERTIME_HOURS");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                assertThatThrownBy(() -> payrollFormula.validateFormula(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }

            @Test
            @DisplayName("빈 변수 맵을 제공하면 검증이 실패한다")
            void shouldFailValidation_whenVariableMapIsEmpty() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();

                assertThatThrownBy(() -> payrollFormula.validateFormula(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }
        }
    }

    @Nested
    @DisplayName("계산식 계산")
    class CalculateFormula {
        @Nested
        @DisplayName("유효한 계산식인 경우")
        class GivenValidFormula {
            @Test
            @DisplayName("계산 결과를 반환한다")
            void shouldReturnCalculationResult_whenFormulaIsValid() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.5");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                BigDecimal result = payrollFormula.calculate(variableValues);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
            }

            @Test
            @DisplayName("복잡한 계산식도 올바르게 계산한다")
            void shouldCalculateComplexFormula_correctly() {
                Formula formula = Formula.of(PayItemFormulaType.OVERTIME_ALLOWANCE, "(BASE_SALARY + OVERTIME_HOURS) * 1.5");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));
                variableValues.put("OVERTIME_HOURS", BigDecimal.valueOf(100));

                BigDecimal result = payrollFormula.calculate(variableValues);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1650.00));
            }
        }

        @Nested
        @DisplayName("계산식이 null인 경우")
        class GivenNullFormula {
            @Test
            @DisplayName("예외를 던진다")
            void shouldThrowException_whenFormulaIsNull() {
                PayrollFormula payrollFormula = PayrollFormula.of(1L, null);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                assertThatThrownBy(() -> payrollFormula.calculate(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식이 설정되지 않았습니다");
            }
        }

        @Nested
        @DisplayName("변수가 부족한 경우")
        class GivenMissingVariable {
            @Test
            @DisplayName("계산식에 필요한 변수가 없으면 예외를 던진다")
            void shouldThrowException_whenRequiredVariableIsMissing() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * OVERTIME_HOURS");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                assertThatThrownBy(() -> payrollFormula.calculate(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }

            @Test
            @DisplayName("빈 변수 맵을 제공하면 예외를 던진다")
            void shouldThrowException_whenVariableMapIsEmpty() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();

                assertThatThrownBy(() -> payrollFormula.calculate(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }

            @Test
            @DisplayName("여러 변수 중 일부만 제공하면 예외를 던진다")
            void shouldThrowException_whenSomeVariablesMissing() {
                Formula formula = Formula.of(PayItemFormulaType.OVERTIME_ALLOWANCE, "(BASE_SALARY + OVERTIME_HOURS) * 1.5");
                PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

                Map<String, BigDecimal> variableValues = new HashMap<>();
                variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

                assertThatThrownBy(() -> payrollFormula.calculate(variableValues))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }
        }
    }

    @Nested
    @DisplayName("계산식 엣지 케이스")
    class FormulaEdgeCases {
        @Test
        @DisplayName("0으로 나누면 ArithmeticException을 던진다")
        void shouldThrow_whenDividingByZero() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY / 0");
            PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

            Map<String, BigDecimal> variableValues = new HashMap<>();
            variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

            assertThatThrownBy(() -> payrollFormula.calculate(variableValues))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("음수 값을 사용한 계산식도 계산할 수 있다")
        void shouldCalculate_whenUsingNegativeValues() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * -1");
            PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

            Map<String, BigDecimal> variableValues = new HashMap<>();
            variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

            BigDecimal result = payrollFormula.calculate(variableValues);

            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(-1000.00));
        }

        @Test
        @DisplayName("복잡한 수학 함수를 사용한 계산식도 계산할 수 있다")
        void shouldCalculate_whenUsingMathFunctions() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "log(BASE_SALARY)");
            PayrollFormula payrollFormula = PayrollFormula.of(1L, formula);

            Map<String, BigDecimal> variableValues = new HashMap<>();
            variableValues.put("BASE_SALARY", BigDecimal.valueOf(1000));

            BigDecimal result = payrollFormula.calculate(variableValues);

            assertThat(result).isNotNull();
        }
    }
}
