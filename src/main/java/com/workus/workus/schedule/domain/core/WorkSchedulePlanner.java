package com.workus.workus.schedule.domain.core;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.schedule.domain.exception.ScheduleConflictException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class WorkSchedulePlanner {
    public WorkSchedule planCreation(
            PlanCreationParam param,
            Collection<WorkSchedule> aroundSchedules
    ) {
        WorkSchedule candidate = new WorkSchedule(
                IdGenerator.nextId()
                , param.storeUserId()
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