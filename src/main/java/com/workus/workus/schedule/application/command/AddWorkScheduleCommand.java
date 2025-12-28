package com.workus.workus.schedule.application.command;


import com.workus.workus.schedule.domain.core.TimeRange;
import com.workus.workus.schedule.domain.core.WorkScheduleSource;

import java.time.LocalDate;

public record AddWorkScheduleCommand(
        Long storeUserId,
        LocalDate scheduleDate,
        TimeRange workTime,
        TimeRange breakTime,
        WorkScheduleSource source
) {
}