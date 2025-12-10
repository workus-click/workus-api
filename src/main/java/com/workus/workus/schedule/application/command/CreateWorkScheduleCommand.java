package com.workus.workus.schedule.application.command;


import java.time.LocalDate;
import java.time.LocalTime;

public record CreateWorkScheduleCommand(
        Long storeUserId,
        LocalDate scheduleDate,
        LocalTime workStart,
        LocalTime workEnd,
        LocalTime breakStart,
        LocalTime breakEnd,
        String source,
        Long workTimeId
) {
}

