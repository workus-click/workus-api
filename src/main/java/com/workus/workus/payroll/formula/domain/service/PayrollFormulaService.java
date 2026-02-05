package com.workus.workus.payroll.formula.domain.service;

import com.workus.workus.payroll.formula.domain.model.PayrollFormula;
import com.workus.workus.payroll.formula.domain.model.PayrollFormulaVersion;
import com.workus.workus.payroll.formula.domain.model.Formula;
import com.workus.workus.payroll.formula.domain.model.FormulaCategory;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.model.FormulaVariable;
import com.workus.workus.payroll.formula.domain.repository.FormulaVariableRepository;
import com.workus.workus.payroll.formula.domain.repository.PayrollFormulaRepository;
import com.workus.workus.payroll.formula.domain.repository.PayrollFormulaVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 급여 계산식 생성 및 버전 관리 서비스
 * 
 * - 계산식(PayrollFormula)은 immutable하며 INSERT만 가능
 * - 계산식 추가/삭제 시 새로운 버전(PayrollFormulaVersion)이 생성됨
 * - 버전은 각 FormulaType별 계산식 ID를 컬럼으로 저장
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollFormulaService {
    private final PayrollFormulaRepository payrollFormulaRepository;
    private final PayrollFormulaVersionRepository versionRepository;
    private final FormulaVariableRepository formulaVariableRepository;

    /**
     * 새로운 급여 계산식을 추가하고 새 버전을 생성합니다.
     * 
     * @param storeId 매장 ID
     * @param formulaType 계산식 타입
     * @param expression 계산식 표현식
     * @return 생성된 버전
     * @throws IllegalArgumentException 기본값이 유효하지 않거나 계산식이 유효하지 않은 경우
     */
    public PayrollFormulaVersion addFormula(
            Long storeId,
            FormulaType formulaType,
            String expression
    ) {
        // 기본값 체크
        validateDefaultValues(storeId, formulaType, expression);

        // Formula 생성
        Formula formula = Formula.of(formulaType, expression);

        // 계산식 유효성 체크
        validateFormulaExpression(storeId, formula);

        // 현재 최신 버전 조회
        Optional<PayrollFormulaVersion> latestVersionOpt = versionRepository.findLatestByStoreId(storeId);
        
        // 현재 버전에 같은 타입의 계산식이 있는지 확인
        if (latestVersionOpt.isPresent() && latestVersionOpt.get().hasFormula(formulaType)) {
            throw new IllegalArgumentException(
                    String.format("현재 버전에 이미 같은 타입의 계산식이 존재합니다. formulaType: %s", 
                            ((Enum<?>) formulaType).name())
            );
        }

        // 새 계산식 생성 및 저장 (immutable - INSERT만)
        PayrollFormula newFormula = PayrollFormula.of(storeId, formula);
        payrollFormulaRepository.save(newFormula);

        // 새 버전 생성
        PayrollFormulaVersion newVersion;
        if (latestVersionOpt.isPresent()) {
            // 기존 버전 복사 후 새 계산식 ID 설정
            newVersion = latestVersionOpt.get().createNextVersion();
        } else {
            // 첫 번째 버전 생성
            newVersion = PayrollFormulaVersion.createFirstVersion(storeId);
        }
        newVersion.setFormulaId(formulaType, newFormula.getId());

        return versionRepository.save(newVersion);
    }

    /**
     * 계산식을 미사용 처리하고 새 버전을 생성합니다.
     * (실제 계산식은 삭제되지 않고, 새 버전에서 해당 타입의 ID가 null이 됨)
     * 
     * @param storeId 매장 ID
     * @param formulaType 미사용 처리할 계산식 타입
     * @return 생성된 버전
     * @throws IllegalArgumentException 현재 버전에 해당 타입의 계산식이 없는 경우
     */
    public PayrollFormulaVersion deactivateFormula(Long storeId, FormulaType formulaType) {
        // 현재 최신 버전 조회
        PayrollFormulaVersion latestVersion = versionRepository.findLatestByStoreId(storeId)
                .orElseThrow(() -> new IllegalArgumentException("버전이 존재하지 않습니다. storeId: " + storeId));

        // 현재 버전에 해당 타입의 계산식이 있는지 확인
        if (!latestVersion.hasFormula(formulaType)) {
            throw new IllegalArgumentException(
                    String.format("현재 버전에 해당 타입의 계산식이 없습니다. formulaType: %s", 
                            ((Enum<?>) formulaType).name())
            );
        }

        // 새 버전 생성 (기존 버전 복사 후 해당 타입 null로 설정)
        PayrollFormulaVersion newVersion = latestVersion.createNextVersion();
        newVersion.setFormulaId(formulaType, null);

        return versionRepository.save(newVersion);
    }

    /**
     * 계산식을 교체합니다 (새 계산식 생성 후 해당 타입의 ID 교체)
     * 
     * @param storeId 매장 ID
     * @param formulaType 교체할 계산식 타입
     * @param expression 새 계산식 표현식
     * @return 생성된 버전
     */
    public PayrollFormulaVersion replaceFormula(
            Long storeId,
            FormulaType formulaType,
            String expression
    ) {
        // 기본값 체크
        validateDefaultValues(storeId, formulaType, expression);

        // 현재 최신 버전 조회
        PayrollFormulaVersion latestVersion = versionRepository.findLatestByStoreId(storeId)
                .orElseThrow(() -> new IllegalArgumentException("버전이 존재하지 않습니다. storeId: " + storeId));

        // 현재 버전에 해당 타입의 계산식이 있는지 확인
        if (!latestVersion.hasFormula(formulaType)) {
            throw new IllegalArgumentException(
                    String.format("현재 버전에 해당 타입의 계산식이 없습니다. formulaType: %s", 
                            ((Enum<?>) formulaType).name())
            );
        }

        // Formula 생성
        Formula formula = Formula.of(formulaType, expression);

        // 계산식 유효성 체크
        validateFormulaExpression(storeId, formula);

        // 새 계산식 생성 및 저장 (immutable - INSERT만)
        PayrollFormula newFormula = PayrollFormula.of(storeId, formula);
        payrollFormulaRepository.save(newFormula);

        // 새 버전 생성 (기존 버전 복사 후 해당 타입의 ID 교체)
        PayrollFormulaVersion newVersion = latestVersion.createNextVersion();
        newVersion.setFormulaId(formulaType, newFormula.getId());

        return versionRepository.save(newVersion);
    }

    /**
     * 매장의 최신 버전 조회
     */
    @Transactional(readOnly = true)
    public Optional<PayrollFormulaVersion> getLatestVersion(Long storeId) {
        return versionRepository.findLatestByStoreId(storeId);
    }

    /**
     * 매장의 모든 버전 조회
     */
    @Transactional(readOnly = true)
    public List<PayrollFormulaVersion> getAllVersions(Long storeId) {
        return versionRepository.findAllByStoreId(storeId);
    }

    /**
     * 기본값 유효성 검증
     */
    private void validateDefaultValues(Long storeId, FormulaType formulaType, String expression) {
        if (storeId == null) {
            throw new IllegalArgumentException("매장 ID는 필수입니다.");
        }
        if (formulaType == null) {
            throw new IllegalArgumentException("계산식 타입은 필수입니다.");
        }
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("계산식 표현식은 필수입니다.");
        }
    }

    /**
     * 계산식 표현식 유효성 검증
     */
    private void validateFormulaExpression(Long storeId, Formula formula) {
        // FormulaType의 카테고리에 해당하는 변수 목록 조회
        FormulaCategory category = formula.getFormulaType().getFormulaCategory();
        String codeType = category.name();

        List<FormulaVariable> variables = 
                formulaVariableRepository.findByStoreIdAndCodeType(storeId, codeType);

        // 허용된 변수 코드 목록 추출
        Set<String> allowedVariables = variables.stream()
                .map(FormulaVariable::getCode)
                .collect(java.util.stream.Collectors.toSet());

        // 테스트용 변수 값 생성 (모든 변수를 1로 설정)
        Map<String, BigDecimal> testValues = new HashMap<>();
        for (String variable : allowedVariables) {
            testValues.put(variable, BigDecimal.ONE);
        }

        // 계산식 유효성 검증 (calculate 메서드가 내부적으로 validateSyntax를 호출)
        try {
            formula.calculate(allowedVariables, testValues);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("계산식이 유효하지 않습니다: " + e.getMessage(), e);
        }
    }
}
