package com.workus.workus.payroll.formula.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddFormulaRequest(
        @NotNull(message = "매장 ID는 필수입니다.")
        Long storeId,

        @NotBlank(message = "계산식 타입은 필수입니다.")
        String formulaType,

        @NotBlank(message = "계산식 표현식은 필수입니다.")
        String expression
) {}
