package com.workus.workus.payroll.formula.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Formula VO 테스트")
class FormulaTest {

    @Nested
    @DisplayName("생성")
    class Creation {
        @Test
        @DisplayName("정적 팩토리 메서드로 생성할 수 있다")
        void shouldCreate_withStaticFactoryMethod() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");

            assertThat(formula).isNotNull();
            assertThat(formula.getFormulaType()).isEqualTo(PayItemFormulaType.BASE_SALARY);
            assertThat(formula.getExpression()).isEqualTo("BASE_SALARY * 1.0");
        }

        @Test
        @DisplayName("생성자로 생성할 수 있다")
        void shouldCreate_withConstructor() {
            Formula formula = new Formula(PayItemFormulaType.OVERTIME_ALLOWANCE, "HOURS * RATE");

            assertThat(formula.getFormulaType()).isEqualTo(PayItemFormulaType.OVERTIME_ALLOWANCE);
            assertThat(formula.getExpression()).isEqualTo("HOURS * RATE");
        }
    }

    @Nested
    @DisplayName("불변성")
    class Immutability {
        @Test
        @DisplayName("생성 후 상태를 변경할 수 없다")
        void shouldBeImmutable_afterCreation() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");

            // Formula에는 setter가 없으므로 컴파일 타임에 불변성이 보장됨
            // 이 테스트는 문서화 목적
            assertThat(formula.getFormulaType()).isEqualTo(PayItemFormulaType.BASE_SALARY);
            assertThat(formula.getExpression()).isEqualTo("BASE_SALARY * 1.0");
        }
    }

    @Nested
    @DisplayName("계산")
    class Calculate {

        @Nested
        @DisplayName("단순 계산식")
        class SimpleExpression {
            @Test
            @DisplayName("단일 변수 곱셈을 계산한다")
            void shouldCalculate_singleVariableMultiplication() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.5");
                Set<String> variables = Set.of("BASE_SALARY");
                Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(2000));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(3000.00));
            }

            @Test
            @DisplayName("단일 변수 덧셈을 계산한다")
            void shouldCalculate_singleVariableAddition() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY + 500");
                Set<String> variables = Set.of("BASE_SALARY");
                Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(2000));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(2500.00));
            }
        }

        @Nested
        @DisplayName("복합 계산식")
        class ComplexExpression {
            @Test
            @DisplayName("여러 변수를 사용한 계산식을 계산한다")
            void shouldCalculate_multipleVariables() {
                Formula formula = Formula.of(
                        PayItemFormulaType.OVERTIME_ALLOWANCE,
                        "BASE_SALARY + OVERTIME_HOURS * OVERTIME_RATE"
                );
                Set<String> variables = Set.of("BASE_SALARY", "OVERTIME_HOURS", "OVERTIME_RATE");
                Map<String, BigDecimal> values = new HashMap<>();
                values.put("BASE_SALARY", BigDecimal.valueOf(2000000));
                values.put("OVERTIME_HOURS", BigDecimal.valueOf(10));
                values.put("OVERTIME_RATE", BigDecimal.valueOf(15000));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(2150000.00));
            }

            @Test
            @DisplayName("괄호가 포함된 계산식을 계산한다")
            void shouldCalculate_withParentheses() {
                Formula formula = Formula.of(
                        PayItemFormulaType.OVERTIME_ALLOWANCE,
                        "(BASE_SALARY + BONUS) * TAX_RATE"
                );
                Set<String> variables = Set.of("BASE_SALARY", "BONUS", "TAX_RATE");
                Map<String, BigDecimal> values = new HashMap<>();
                values.put("BASE_SALARY", BigDecimal.valueOf(3000000));
                values.put("BONUS", BigDecimal.valueOf(500000));
                values.put("TAX_RATE", BigDecimal.valueOf(0.1));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(350000.00));
            }

            @Test
            @DisplayName("나눗셈이 포함된 계산식을 계산한다")
            void shouldCalculate_withDivision() {
                Formula formula = Formula.of(
                        PayItemFormulaType.BASE_SALARY,
                        "ANNUAL_SALARY / 12"
                );
                Set<String> variables = Set.of("ANNUAL_SALARY");
                Map<String, BigDecimal> values = Map.of("ANNUAL_SALARY", BigDecimal.valueOf(36000000));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(3000000.00));
            }
        }

        @Nested
        @DisplayName("소수점 처리")
        class DecimalHandling {
            @Test
            @DisplayName("결과를 소수점 2자리로 반올림한다")
            void shouldRound_toTwoDecimalPlaces() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY / 3");
                Set<String> variables = Set.of("BASE_SALARY");
                Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(10000));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(3333.33));
            }

            @Test
            @DisplayName("소수점 값으로 계산할 수 있다")
            void shouldCalculate_withDecimalValues() {
                Formula formula = Formula.of(
                        DeductItemFormulaType.INCOME_TAX,
                        "GROSS_SALARY * TAX_RATE"
                );
                Set<String> variables = Set.of("GROSS_SALARY", "TAX_RATE");
                Map<String, BigDecimal> values = new HashMap<>();
                values.put("GROSS_SALARY", BigDecimal.valueOf(3500000));
                values.put("TAX_RATE", BigDecimal.valueOf(0.033));

                BigDecimal result = formula.calculate(variables, values);

                assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(115500.00));
            }
        }
    }

    @Nested
    @DisplayName("검증")
    class Validation {

        @Nested
        @DisplayName("변수 누락")
        class MissingVariable {
            @Test
            @DisplayName("허용된 변수에 대한 값이 없으면 예외를 던진다")
            void shouldThrow_whenVariableValueMissing() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * 1.0");
                Set<String> variables = Set.of("BASE_SALARY");
                Map<String, BigDecimal> values = new HashMap<>(); // 빈 맵

                assertThatThrownBy(() -> formula.calculate(variables, values))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("누락된 변수");
            }

            @Test
            @DisplayName("일부 변수 값이 누락되면 예외를 던진다")
            void shouldThrow_whenSomeVariableValuesMissing() {
                Formula formula = Formula.of(
                        PayItemFormulaType.OVERTIME_ALLOWANCE,
                        "BASE_SALARY + BONUS"
                );
                Set<String> variables = Set.of("BASE_SALARY", "BONUS");
                Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(2000000));
                // BONUS 누락

                assertThatThrownBy(() -> formula.calculate(variables, values))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("누락된 변수")
                        .hasMessageContaining("BONUS");
            }
        }

        @Nested
        @DisplayName("문법 오류")
        class SyntaxError {
            @Test
            @DisplayName("정의되지 않은 변수를 사용하면 예외를 던진다")
            void shouldThrow_whenUndefinedVariable() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY + UNKNOWN_VAR");
                Set<String> variables = Set.of("BASE_SALARY");
                Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(2000000));

                assertThatThrownBy(() -> formula.calculate(variables, values))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }

            @Test
            @DisplayName("괄호가 맞지 않는 계산식은 예외를 던진다")
            void shouldThrow_whenParenthesesUnmatched() {
                Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "(BASE_SALARY * 1.0");
                Set<String> variables = Set.of("BASE_SALARY");
                Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(2000000));

                assertThatThrownBy(() -> formula.calculate(variables, values))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("잘못된 계산식 문법");
            }
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {
        @Test
        @DisplayName("0으로 나누면 ArithmeticException을 던진다")
        void shouldThrow_whenDividingByZero() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY / 0");
            Set<String> variables = Set.of("BASE_SALARY");
            Map<String, BigDecimal> values = Map.of("BASE_SALARY", BigDecimal.valueOf(1000));

            // exp4j는 Infinity를 반환하지만 BigDecimal 변환 시 ArithmeticException 발생
            assertThatThrownBy(() -> formula.calculate(variables, values))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("음수 결과를 반환할 수 있다")
        void shouldReturn_negativeResult() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY - DEDUCTION");
            Set<String> variables = Set.of("BASE_SALARY", "DEDUCTION");
            Map<String, BigDecimal> values = new HashMap<>();
            values.put("BASE_SALARY", BigDecimal.valueOf(1000));
            values.put("DEDUCTION", BigDecimal.valueOf(2000));

            BigDecimal result = formula.calculate(variables, values);

            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(-1000.00));
        }

        @Test
        @DisplayName("0 값으로 계산할 수 있다")
        void shouldCalculate_withZeroValue() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "BASE_SALARY * MULTIPLIER");
            Set<String> variables = Set.of("BASE_SALARY", "MULTIPLIER");
            Map<String, BigDecimal> values = new HashMap<>();
            values.put("BASE_SALARY", BigDecimal.valueOf(2000000));
            values.put("MULTIPLIER", BigDecimal.ZERO);

            BigDecimal result = formula.calculate(variables, values);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("상수만 있는 계산식도 계산할 수 있다")
        void shouldCalculate_constantOnlyExpression() {
            Formula formula = Formula.of(PayItemFormulaType.BASE_SALARY, "1000 + 500");
            Set<String> variables = Set.of();
            Map<String, BigDecimal> values = new HashMap<>();

            BigDecimal result = formula.calculate(variables, values);

            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
        }
    }

    @Nested
    @DisplayName("FormulaType별 테스트")
    class FormulaTypeSpecific {

        @Test
        @DisplayName("PayItemFormulaType으로 지급항목 계산식을 생성한다")
        void shouldCreate_withPayItemFormulaType() {
            Formula formula = Formula.of(PayItemFormulaType.NIGHT_SHIFT_ALLOWANCE, "WORK_HOURS * NIGHT_RATE");

            assertThat(formula.getFormulaType()).isInstanceOf(PayItemFormulaType.class);
            assertThat(formula.getFormulaType().getFormulaCategory()).isEqualTo(FormulaCategory.PAY_ITEM);
        }

        @Test
        @DisplayName("DeductItemFormulaType으로 공제항목 계산식을 생성한다")
        void shouldCreate_withDeductItemFormulaType() {
            Formula formula = Formula.of(DeductItemFormulaType.NATIONAL_PENSION, "GROSS_SALARY * 0.045");

            assertThat(formula.getFormulaType()).isInstanceOf(DeductItemFormulaType.class);
            assertThat(formula.getFormulaType().getFormulaCategory()).isEqualTo(FormulaCategory.DEDUCT_ITEM);
        }
    }
}
