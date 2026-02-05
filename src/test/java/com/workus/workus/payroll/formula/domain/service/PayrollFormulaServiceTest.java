package com.workus.workus.payroll.formula.domain.service;

import com.github.f4b6a3.tsid.TsidFactory;
import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.payroll.formula.domain.model.PayrollFormula;
import com.workus.workus.payroll.formula.domain.model.PayrollFormulaVersion;
import com.workus.workus.payroll.formula.domain.model.Formula;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.model.FormulaVariable;
import com.workus.workus.payroll.formula.domain.model.PayItemFormulaType;
import com.workus.workus.payroll.formula.domain.repository.FormulaVariableRepository;
import com.workus.workus.payroll.formula.domain.repository.PayrollFormulaRepository;
import com.workus.workus.payroll.formula.domain.repository.PayrollFormulaVersionRepository;
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
class PayrollFormulaServiceTest {

    @BeforeAll
    static void initIdGenerator() {
        new IdGenerator(TsidFactory.newInstance256());
    }

    @Mock
    private PayrollFormulaRepository payrollFormulaRepository;

    @Mock
    private PayrollFormulaVersionRepository versionRepository;

    @Mock
    private FormulaVariableRepository formulaVariableRepository;

    @InjectMocks
    private PayrollFormulaService payrollFormulaService;

    @Nested
    @DisplayName("계산식 추가 (addFormula)")
    class AddFormula {
        @Nested
        @DisplayName("정상 케이스")
        class GivenValidInput {
            @Test
            @DisplayName("첫 번째 버전으로 계산식을 추가할 수 있다")
            void shouldAddFormula_whenFirstVersion() {
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.empty());

                PayrollFormula savedFormula = PayrollFormula.of(storeId, Formula.of(formulaType, expression));
                when(payrollFormulaRepository.save(any(PayrollFormula.class)))
                        .thenReturn(savedFormula);

                when(versionRepository.save(any(PayrollFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                PayrollFormulaVersion result = payrollFormulaService.addFormula(storeId, formulaType, expression);

                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(1);
                assertThat(result.getFormulaId(formulaType)).isNotNull();
                verify(payrollFormulaRepository).save(any(PayrollFormula.class));
                verify(versionRepository).save(any(PayrollFormulaVersion.class));
            }

            @Test
            @DisplayName("기존 버전에 계산식을 추가하면 새 버전이 생성된다")
            void shouldCreateNewVersion_whenAddingToExistingVersion() {
                Long storeId = 1L;
                FormulaType existingType = PayItemFormulaType.BASE_SALARY;
                FormulaType newType = PayItemFormulaType.OVERTIME_ALLOWANCE;
                String expression = "OVERTIME_HOURS * 1.5";

                PayrollFormulaVersion existingVersion = PayrollFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(existingType, 100L);

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "OVERTIME_HOURS");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                PayrollFormula newFormula = PayrollFormula.of(storeId, Formula.of(newType, expression));
                when(payrollFormulaRepository.save(any(PayrollFormula.class)))
                        .thenReturn(newFormula);

                when(versionRepository.save(any(PayrollFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                PayrollFormulaVersion result = payrollFormulaService.addFormula(storeId, newType, expression);

                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(2);
                assertThat(result.getFormulaId(existingType)).isEqualTo(100L);
                assertThat(result.getFormulaId(newType)).isNotNull();
                verify(versionRepository).save(any(PayrollFormulaVersion.class));
            }
        }

        @Nested
        @DisplayName("기본값 검증 실패 케이스")
        class GivenInvalidDefaultValues {
            @Test
            @DisplayName("storeId가 null이면 예외를 던진다")
            void shouldThrowException_whenStoreIdIsNull() {
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                assertThatThrownBy(() -> payrollFormulaService.addFormula(null, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("매장 ID는 필수입니다");
            }

            @Test
            @DisplayName("formulaType이 null이면 예외를 던진다")
            void shouldThrowException_whenFormulaTypeIsNull() {
                Long storeId = 1L;
                String expression = "BASE_SALARY * 1.0";

                assertThatThrownBy(() -> payrollFormulaService.addFormula(storeId, null, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 타입은 필수입니다");
            }

            @Test
            @DisplayName("expression이 null이면 예외를 던진다")
            void shouldThrowException_whenExpressionIsNull() {
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                assertThatThrownBy(() -> payrollFormulaService.addFormula(storeId, formulaType, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("계산식 표현식은 필수입니다");
            }

            @Test
            @DisplayName("expression이 빈 문자열이면 예외를 던진다")
            void shouldThrowException_whenExpressionIsBlank() {
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "   ";

                assertThatThrownBy(() -> payrollFormulaService.addFormula(storeId, formulaType, expression))
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
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String expression = "BASE_SALARY * 1.0";

                PayrollFormulaVersion existingVersion = PayrollFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                assertThatThrownBy(() -> payrollFormulaService.addFormula(storeId, formulaType, expression))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("현재 버전에 이미 같은 타입의 계산식이 존재합니다");

                verify(payrollFormulaRepository, never()).save(any());
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
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                PayrollFormulaVersion existingVersion = PayrollFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));
                when(versionRepository.save(any(PayrollFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                PayrollFormulaVersion result = payrollFormulaService.deactivateFormula(storeId, formulaType);

                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(2);
                assertThat(result.getFormulaId(formulaType)).isNull();
                verify(versionRepository).save(any(PayrollFormulaVersion.class));
            }
        }

        @Nested
        @DisplayName("버전이 존재하지 않는 경우")
        class GivenNoVersion {
            @Test
            @DisplayName("버전이 존재하지 않으면 예외를 던진다")
            void shouldThrowException_whenVersionDoesNotExist() {
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;

                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.empty());

                assertThatThrownBy(() -> payrollFormulaService.deactivateFormula(storeId, formulaType))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("버전이 존재하지 않습니다");
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
                Long storeId = 1L;
                FormulaType formulaType = PayItemFormulaType.BASE_SALARY;
                String newExpression = "BASE_SALARY * 1.5";

                PayrollFormulaVersion existingVersion = PayrollFormulaVersion.createFirstVersion(storeId);
                existingVersion.setFormulaId(formulaType, 100L);

                List<FormulaVariable> variables = createFormulaVariables(storeId, "PAY_ITEM", "BASE_SALARY");
                when(formulaVariableRepository.findByStoreIdAndCodeType(storeId, "PAY_ITEM"))
                        .thenReturn(variables);
                when(versionRepository.findLatestByStoreId(storeId))
                        .thenReturn(Optional.of(existingVersion));

                PayrollFormula newFormula = PayrollFormula.of(storeId, Formula.of(formulaType, newExpression));
                when(payrollFormulaRepository.save(any(PayrollFormula.class)))
                        .thenReturn(newFormula);
                when(versionRepository.save(any(PayrollFormulaVersion.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                PayrollFormulaVersion result = payrollFormulaService.replaceFormula(storeId, formulaType, newExpression);

                assertThat(result).isNotNull();
                assertThat(result.getVersionNumber()).isEqualTo(2);
                assertThat(result.getFormulaId(formulaType)).isNotNull();
                assertThat(result.getFormulaId(formulaType)).isNotEqualTo(100L);
                verify(payrollFormulaRepository).save(any(PayrollFormula.class));
                verify(versionRepository).save(any(PayrollFormulaVersion.class));
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
