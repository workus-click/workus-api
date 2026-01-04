package com.workus.workus.attend.schedule.application.command;

import com.workus.workus.attend.schedule.domain.core.WorkAndBreakTime;

import java.time.LocalDate;

public record ChangeWorkScheduleCommand (
    Long workScheduleId,
    LocalDate scheduleDate,
    WorkAndBreakTime workAndBreakTime
){}