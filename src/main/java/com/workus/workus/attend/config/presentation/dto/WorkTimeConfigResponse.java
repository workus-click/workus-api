package com.workus.workus.attend.config.presentation.dto;

import com.workus.workus.attend.config.domain.model.EmployeeType;
import com.workus.workus.attend.config.domain.model.WorkTimeConfig;

import java.time.LocalTime;

public record WorkTimeConfigResponse(
        Long id,
        Long storeId,
        EmployeeType workerType,
        String title,
        LocalTime workStartTime,
        LocalTime workEndTime,
        LocalTime breakStartTime,
        LocalTime breakEndTime
) {
    public static WorkTimeConfigResponse from(WorkTimeConfig config) {
        return new WorkTimeConfigResponse(
                config.getId(),
                config.getStoreId(),
                config.getWorkerType(),
                config.getTitle(),
                config.getWorkTime().start(),
                config.getWorkTime().end(),
                config.getBreakTime() != null ? config.getBreakTime().start() : null,
                config.getBreakTime() != null ? config.getBreakTime().end() : null
        );
    }
}
