package com.workus.workus.payroll.formula.presentation.dto;

import com.workus.workus.payroll.formula.domain.model.DeductItemFormulaType;
import com.workus.workus.payroll.formula.domain.model.PayItemFormulaType;
import com.workus.workus.payroll.formula.domain.model.PayrollFormula;
import com.workus.workus.payroll.formula.domain.model.PayrollFormulaVersion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record FormulaVersionResponse(
        Long versionId,
        Long storeId,
        Integer versionNumber,
        List<FormulaDetailResponse> formulas,
        LocalDateTime createdAt
) {
    /**
     * 버전 정보만으로 응답 생성 (formulaId만 포함, expression 없음)
     */
    public static FormulaVersionResponse fromVersionOnly(PayrollFormulaVersion version) {
        List<FormulaDetailResponse> formulas = new ArrayList<>();

        for (PayItemFormulaType type : PayItemFormulaType.values()) {
            Long formulaId = version.getFormulaId(type);
            if (formulaId != null) {
                formulas.add(new FormulaDetailResponse(formulaId, type.name(), type.getFormulaCategory().name(), null));
            }
        }

        for (DeductItemFormulaType type : DeductItemFormulaType.values()) {
            Long formulaId = version.getFormulaId(type);
            if (formulaId != null) {
                formulas.add(new FormulaDetailResponse(formulaId, type.name(), type.getFormulaCategory().name(), null));
            }
        }

        return new FormulaVersionResponse(
                version.getId(),
                version.getStoreId(),
                version.getVersionNumber(),
                formulas,
                version.getCreatedAt()
        );
    }

    /**
     * 버전 정보와 계산식 상세 정보로 응답 생성 (expression 포함)
     */
    public static FormulaVersionResponse from(PayrollFormulaVersion version, List<PayrollFormula> formulaList) {
        Map<Long, PayrollFormula> formulaMap = formulaList.stream()
                .collect(Collectors.toMap(PayrollFormula::getId, Function.identity()));

        List<FormulaDetailResponse> formulas = new ArrayList<>();

        for (PayItemFormulaType type : PayItemFormulaType.values()) {
            Long formulaId = version.getFormulaId(type);
            if (formulaId != null) {
                PayrollFormula formula = formulaMap.get(formulaId);
                if (formula != null) {
                    formulas.add(FormulaDetailResponse.from(formula));
                }
            }
        }

        for (DeductItemFormulaType type : DeductItemFormulaType.values()) {
            Long formulaId = version.getFormulaId(type);
            if (formulaId != null) {
                PayrollFormula formula = formulaMap.get(formulaId);
                if (formula != null) {
                    formulas.add(FormulaDetailResponse.from(formula));
                }
            }
        }

        return new FormulaVersionResponse(
                version.getId(),
                version.getStoreId(),
                version.getVersionNumber(),
                formulas,
                version.getCreatedAt()
        );
    }
}
