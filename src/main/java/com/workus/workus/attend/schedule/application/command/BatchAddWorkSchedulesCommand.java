package com.workus.workus.attend.schedule.application.command;

import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;

import java.time.LocalDate;
import java.util.Set;

public record BatchAddWorkSchedulesCommand(
        Long storeUserId,
        WorkScheduleSource source,
        Set<ScheduleItem> schedules
) {
    public record ScheduleItem(
            LocalDate scheduleDate,
            TimeRange workTime,
            TimeRange breakTime
    ) {
    }
}
