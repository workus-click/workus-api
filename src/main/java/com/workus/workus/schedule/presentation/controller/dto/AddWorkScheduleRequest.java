package com.workus.workus.schedule.presentation.controller.dto;


import com.workus.workus.common.presentation.validation.IsoDate;
import com.workus.workus.common.presentation.validation.IsoTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddWorkScheduleRequest(
    @NotNull(message = "{schedule.storeUserId.required}")
    Long storeUserId,
    @NotBlank(message = "{schedule.scheduleDate.required}")
    @IsoDate
    String scheduleDate,
    @NotBlank(message = "{schedule.workTimeStart.required}")
    @IsoTime
    String workTimeStart,
    @NotBlank(message = "{schedule.workTimeEnd.required}")
    @IsoTime
    String workTimeEnd,
    @IsoTime
    String breakTimeStart,
    @IsoTime
    String breakTimeEnd
) {
}
