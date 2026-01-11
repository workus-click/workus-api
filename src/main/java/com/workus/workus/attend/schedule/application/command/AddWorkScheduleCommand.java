package com.workus.workus.attend.schedule.application.command;


import com.workus.workus.attend.schedule.domain.core.WorkAndBreakTime;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;

import java.time.LocalDate;

public record AddWorkScheduleCommand(
        Long storeUserId,
        LocalDate scheduleDate,
        WorkAndBreakTime workAndBreakTime,
        WorkScheduleSource source
) {
}