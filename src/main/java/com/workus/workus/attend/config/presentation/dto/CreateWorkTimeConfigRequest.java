package com.workus.workus.attend.config.presentation.dto;

import com.workus.workus.attend.config.domain.model.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CreateWorkTimeConfigRequest(
        @NotNull(message = "매장 ID는 필수입니다.")
        Long storeId,

        @NotNull(message = "직원 구분은 필수입니다.")
        EmployeeType workerType,

        @NotBlank(message = "근무시간 설정명은 필수입니다.")
        String title,

        @NotNull(message = "근무 시작시간은 필수입니다.")
        LocalTime workStartTime,

        @NotNull(message = "근무 종료시간은 필수입니다.")
        LocalTime workEndTime,

        LocalTime breakStartTime,

        LocalTime breakEndTime
) {
}
