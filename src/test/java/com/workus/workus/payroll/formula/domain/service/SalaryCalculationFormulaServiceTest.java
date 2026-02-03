package com.workus.workus.payroll.formula.domain.service;

import com.github.f4b6a3.tsid.TsidFactory;
import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormula;
import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormulaVersion;
import com.workus.workus.payroll.formula.domain.model.Formula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.model.FormulaVariable;
import com.workus.workus.payroll.formula.domain.model.PayItemFormulaType;
import com.workus.workus.payroll.formula.domain.repository.FormulaVariableRepository;
import com.workus.workus.payroll.formula.domain.repository.SalaryCalculationFormulaRepository;
import com.workus.workus.payroll.formula.domain.repository.SalaryCalculationFormulaVersionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SalaryCalculationFormulaServiceTest {

    @BeforeAll
    static void initIdGenerator() {
        // 테스트용 IdGenerator 초기화
        new IdGenerator(TsidFactory.newInstance256());
    }

    @Mock
    private SalaryCalculationFormulaRepository salaryCalculationFormulaRepository;

    @Mock
    private SalaryCalculationFormulaVersionRepository versionRepository;

    @Mock
    private FormulaVariableRepository formulaVariableRepository;

    @InjectMocks
    private SalaryCalculationFormulaService salaryCalculationFormulaService;

    @Nested
    @DisplayName("계산식 추가 (addFormula)")
    class AddFormula {
        @Nested
        @DisplayName("정상 케이스")
        class GivenValidInput {
            @Test
            @DisplayName("첫 번째 버전으로 계산식을 추가할 수 있다")
            void shouldAddFormula_whenFirstVersion() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.empty());

                SalaryCalculationFormula savedFormula = SalaryCalculationFormula.of(storeId, Formula.of(formulaType, expression));
                when(salaryCalculationFormulaRepository.save(any(SalaryCalculationFormula.class)))
                        .thenReturn(savedFormula);

                when(versionRepository.save(any(SalaryCalculationFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                SalaryCalculationFormulaVersion result = salaryCalculationFormulaService.addFormula(storeId, formulaType, expression);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(1);
                assertThat(result.getFormulaId(formulaType)).isNotNull();
                verify(salaryCalculationFormulaRepository).save(any(SalaryCalculationFormula.class));
                verify(versionRepository).save(any(SalaryCalculationFormulaVersion.class));
            }

            @Test
            @DisplayName("기존 버전에 계산식을 추가하면 새 버전이 생성된다")
            void shouldCreateNewVersion_whenAddingToExistingVersion() {
                // given
                Long storeId = 1L;
                FormulaType existingType = PayItemFormulaType.BASE_SALARY;
                FormulaType newType = PayItemFormulaType.OVERTIME_ALLOWANCE;
                String expression = "OVERTIME_HOURS * 1.5";

                // 기존 버전 설정 (BASE_SALARY만 있음)
                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(existingType, 100L);

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "OVERTIME_HOURS");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                SalaryCalculationFormula newFormula = SalaryCalculationFormula.of(storeId, Formula.of(newType, expression));
                when(salaryCalculationFormulaRepository.save(any(SalaryCalculationFormula.class)))
                        .thenReturn(newFormula);

                when(versionRepository.save(any(SalaryCalculationFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                SalaryCalculationFormulaVersion result = salaryCalculationFormulaService.addFormula(storeId, newType, expression);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(2);
                assertThat(result.getFormulaId(existingType)).isEqualTo(100L); // 기존 계산식 유지
                assertThat(result.getFormulaId(newType)).isNotNull(); // 새 계산식 추가됨
                verify(versionRepository).save(any(SalaryCalculationFormulaVersion.class));
            }
        }

        @Nested
        @DisplayName("기본값 검증 실패 케이스")
        class GivenInvalidDefaultValues {
            @Test
            @DisplayName("storeId가 null이면 예외를 던진다")
            void shouldThrowException_whenStoreIdIsNull() {
                // given
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(null, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("매장 ID는 필수입니다");
            }

            @Test
            @DisplayName("formulaType이 null이면 예외를 던진다")
            void shouldThrowException_whenFormulaTypeIsNull() {
                // given
                Long storeId = 1L;
                String expression = "BASE_SALARY * 1.0";

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, null, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 타입은 필수입니다");
            }

            @Test
            @DisplayName("expression이 null이면 예외를 던진다")
            void shouldThrowException_whenExpressionIsNull() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, formulaType, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 표현식은 필수입니다");
            }

            @Test
            @DisplayName("expression이 빈 문자열이면 예외를 던진다")
            void shouldThrowException_whenExpressionIsBlank() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "   ";

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 표현식은 필수입니다");
            }
        }

        @Nested
        @DisplayName("중복 체크 실패 케이스")
        class GivenDuplicateFormula {
            @Test
            @DisplayName("현재 버전에 같은 타입의 계산식이 있으면 예외를 던진다")
            void shouldThrowException_whenSameTypeFormulaExistsInCurrentVersion() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                // 기존 버전에 같은 타입의 계산식이 있음
                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                // validateFormulaExpression을 위한 stubbing
                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("현재 버전에 이미 같은 타입의 계산식이 존재합니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("계산식 유효성 검증 실패 케이스")
        class GivenInvalidFormula {
            @Test
            @DisplayName("잘못된 문법의 계산식이면 예외를 던진다")
            void shouldThrowException_whenFormulaSyntaxIsInvalid() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * +"; // 잘못된 문법

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식이 유효하지 않습니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }

            @Test
            @DisplayName("허용되지 않은 변수를 사용하면 예외를 던진다")
            void shouldThrowException_whenUsingUnauthorizedVariable() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "INVALID_VARIABLE * 1.0"; // 허용되지 않은 변수

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식이 유효하지 않습니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }

            @Test
            @DisplayName("빈 문자열만 있는 계산식이면 예외를 던진다")
            void shouldThrowException_whenExpressionIsEmptyString() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "";

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.addFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 표현식은 필수입니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
                verify(formulaVariableRepository, never()).findByStoreIdAndCodeType(any(), anyString());
            }
        }
    }

    @Nested
    @DisplayName("계산식 미사용 처리 (deactivateFormula)")
    class DeactivateFormula {
        @Nested
        @DisplayName("정상 케이스")
        class GivenValidInput {
            @Test
            @DisplayName("계산식을 미사용 처리하면 새 버전이 생성된다")
            void shouldCreateNewVersion_whenDeactivatingFormula() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));
                when(versionRepository.save(any(SalaryCalculationFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                SalaryCalculationFormulaVersion result = salaryCalculationFormulaService.deactivateFormula(storeId, formulaType);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(2);
                assertThat(result.getFormulaId(formulaType)).isNull();
                verify(versionRepository).save(any(SalaryCalculationFormulaVersion.class));
            }
        }

        @Nested
        @DisplayName("버전이 존재하지 않는 경우")
        class GivenNoVersion {
            @Test
            @DisplayName("버전이 존재하지 않으면 예외를 던진다")
            void shouldThrowException_whenVersionDoesNotExist() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.deactivateFormula(storeId, formulaType))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("버전이 존재하지 않습니다");
            }
        }

        @Nested
        @DisplayName("버전에 해당 타입이 없는 경우")
        class GivenFormulaTypeNotInVersion {
            @Test
            @DisplayName("현재 버전에 해당 타입의 계산식이 없으면 예외를 던진다")
            void shouldThrowException_whenFormulaTypeNotInCurrentVersion() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                // BASE_SALARY를 설정하지 않음

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.deactivateFormula(storeId, formulaType))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("현재 버전에 해당 타입의 계산식이 없습니다");
            }
        }
    }

    @Nested
    @DisplayName("계산식 교체 (replaceFormula)")
    class ReplaceFormula {
        @Nested
        @DisplayName("정상 케이스")
        class GivenValidInput {
            @Test
            @DisplayName("계산식을 교체하면 새 버전이 생성된다")
            void shouldCreateNewVersion_whenReplacingFormula() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String newExpression = "BASE_SALARY * 1.5";

                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                SalaryCalculationFormula newFormula = SalaryCalculationFormula.of(storeId, Formula.of(formulaType, newExpression));
                when(salaryCalculationFormulaRepository.save(any(SalaryCalculationFormula.class)))
                        .thenReturn(newFormula);
                when(versionRepository.save(any(SalaryCalculationFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                SalaryCalculationFormulaVersion result = salaryCalculationFormulaService.replaceFormula(storeId, formulaType, newExpression);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(2);
                assertThat(result.getFormulaId(formulaType)).isNotNull();
                assertThat(result.getFormulaId(formulaType)).isNotEqualTo(100L); // 새 계산식 ID
                verify(salaryCalculationFormulaRepository).save(any(SalaryCalculationFormula.class));
                verify(versionRepository).save(any(SalaryCalculationFormulaVersion.class));
            }
        }

        @Nested
        @DisplayName("버전에 해당 타입이 없는 경우")
        class GivenFormulaTypeNotInVersion {
            @Test
            @DisplayName("현재 버전에 해당 타입이 없으면 예외를 던진다")
            void shouldThrowException_whenFormulaTypeNotExist() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                // BASE_SALARY를 설정하지 않음

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.replaceFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("현재 버전에 해당 타입의 계산식이 없습니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("기본값 검증 실패 케이스")
        class GivenInvalidDefaultValues {
            @Test
            @DisplayName("formulaType이 null이면 예외를 던진다")
            void shouldThrowException_whenFormulaTypeIsNull() {
                // given
                Long storeId = 1L;
                String expression = "BASE_SALARY * 1.0";

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.replaceFormula(storeId, null, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 타입은 필수입니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }

            @Test
            @DisplayName("expression이 null이면 예외를 던진다")
            void shouldThrowException_whenExpressionIsNull() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.replaceFormula(storeId, formulaType, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 표현식은 필수입니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("계산식 유효성 검증 실패 케이스")
        class GivenInvalidFormula {
            @Test
            @DisplayName("잘못된 문법의 계산식이면 예외를 던진다")
            void shouldThrowException_whenFormulaSyntaxIsInvalid() {
                // given
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String invalidExpression = "BASE_SALARY * +"; // 잘못된 문법

                SalaryCalculationFormulaVersion existingVersion = SalaryCalculationFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);

                // when & then
                assertThatThrownBy(() -> salaryCalculationFormulaService.replaceFormula(storeId, formulaType, invalidExpression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식이 유효하지 않습니다");

                verify(salaryCalculationFormulaRepository, never()).save(any());
            }
        }
    }

    private List<FormulaVariable> createFormulaVariables(Long storeId, String codeType, String... codes) {
        List<FormulaVariable> variables = new ArrayList<>();
        for (int i = 0; i < codes.length; i++) {
            FormulaVariable variable = mock(FormulaVariable.class);
            when(variable.getId()).thenReturn((long) (i + 1));
            when(variable.getStoreId()).thenReturn(storeId);
            when(variable.getCodeType()).thenReturn(codeType);
            when(variable.getCode()).thenReturn(codes[i]);
            when(variable.getName()).thenReturn(codes[i] + " Name");
            variables.add(variable);
        }
        return variables;
    }
}
