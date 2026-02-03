package com.workus.workus.attend.config.presentation.dto;

import java.time.LocalTime;

public record UpdateWorkTimeConfigRequest(
        String title,
        LocalTime workStartTime,
        LocalTime workEndTime,
        LocalTime breakStartTime,
        LocalTime breakEndTime
) {
}
