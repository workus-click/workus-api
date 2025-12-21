package com.workus.workus.schedule.domain.service;

import com.workus.workus.schedule.domain.model.TimeRange;
import com.workus.workus.schedule.domain.model.WorkScheduleSource;

import java.time.LocalDate;

public record PlanCreationParam(
        Long storeUserId
        , LocalDate scheduleDate, TimeRange workTime, TimeRange breakTime
        , WorkScheduleSource source, Long workTimeId) {

    // 1) 수동 입력으로 생성하는 경우
    public static PlanCreationParam fromUserInput(
            Long storeUserId,
            LocalDate scheduleDate,
            TimeRange workTime,
            TimeRange breakTime
    ) {
        return new PlanCreationParam(
                storeUserId,
                scheduleDate,
                workTime,
                breakTime,
                WorkScheduleSource.MANUAL,
                null
        );
    }

}