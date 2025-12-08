package com.workus.workus.schedule.domain.service;

import com.workus.workus.schedule.domain.exception.ScheduleConflictException;
import com.workus.workus.schedule.domain.model.WorkSchedule;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class WorkSchedulePlanner {
    public WorkSchedule planCreation(
            PlanCreationParam param,
            Collection<WorkSchedule> aroundSchedules
    ) {
        WorkSchedule candidate = new WorkSchedule(
                param.storeUserId()
                , param.scheduleDate()
                , param.workTime()
                , param.breakTime()
                , param.source()
                , param.workTimeId()
        );
        validateNew(candidate, aroundSchedules);

        return candidate;
    }

    private void validateNew(WorkSchedule candidate, Collection<WorkSchedule> aroundSchedules) {
        if (!isSchedulable(candidate, aroundSchedules)) {
            throw new ScheduleConflictException();
        }
    }

    private boolean isSchedulable(WorkSchedule candidate, Collection<WorkSchedule> aroundSchedules) {
        return aroundSchedules.stream()
                .anyMatch(existing -> existing.workTimeOverlaps(candidate));
    }
}