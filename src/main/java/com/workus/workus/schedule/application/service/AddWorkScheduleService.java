package com.workus.workus.schedule.application.service;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.result.Result;
import com.workus.workus.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.schedule.domain.core.*;
import com.workus.workus.schedule.domain.policy.CalendarLoadPolicy;
import com.workus.workus.schedule.domain.repository.WorkScheduleCalendarLoader;
import com.workus.workus.schedule.domain.repository.WorkScheduleRepository;
import com.workus.workus.schedule.domain.violation.WorkScheduleRuleViolation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class AddWorkScheduleService {
    private final WorkScheduleRepository repository;
    private final WorkScheduleCalendarLoader calendarLoader;
    private final CalendarLoadPolicy calendarLoadPolicy;

    public Result<Long, WorkScheduleRuleViolation.Create> addWorkSchedule(AddWorkScheduleCommand command) {
        DateRange loadRange = calendarLoadPolicy.getLoadRangeForConflictCheck(command.scheduleDate());
        WorkScheduleCalendar calendar = calendarLoader.loadCalendar(command.storeUserId(), loadRange);

        return calendar.addSchedule(IdGenerator.nextId(), command)
                .onSuccess(repository::save)
                .map(WorkSchedule::getId);
    }
}
