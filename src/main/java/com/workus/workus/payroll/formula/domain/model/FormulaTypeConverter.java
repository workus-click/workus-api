package com.workus.workus.payroll.formula.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FormulaTypeConverter implements AttributeConverter<FormulaType, String> {

    @Override
    public String convertToDatabaseColumn(FormulaType formulaType) {
        if (formulaType == null) {
            return null;
        }
        // FormulaType의 name()을 사용 (예: "BASE_SALARY", "INCOME_TAX")
        return ((Enum<?>) formulaType).name();
    }

    @Override
    public FormulaType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        
        // PayItemFormulaType과 DeductItemFormulaType에서 찾기
        try {
            // PayItemFormulaType에서 찾기
            try {
                return PayItemFormulaType.valueOf(dbData);
            } catch (IllegalArgumentException e) {
                // PayItemFormulaType에 없으면 DeductItemFormulaType에서 찾기
                return DeductItemFormulaType.valueOf(dbData);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("알 수 없는 FormulaType: " + dbData, e);
        }
    }
}
