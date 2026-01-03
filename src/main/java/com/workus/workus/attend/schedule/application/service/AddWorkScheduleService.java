package com.workus.workus.attend.schedule.application.service;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand;
import com.workus.workus.attend.schedule.domain.core.WorkSchedule;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleCalendar;
import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.result.Result;
import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.domain.policy.CalendarLoadPolicy;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleCalendarLoader;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleRepository;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

    public List<Result<Long, FailedAddWorkSchedule>> addWorkSchedules(BatchAddWorkSchedulesCommand command) {
        Set<BatchAddWorkSchedulesCommand.ScheduleItem> schedules = command.schedules();
        List<DateRange> loadRanges = schedules.stream()
                .map(scheduleItem -> calendarLoadPolicy.getLoadRangeForConflictCheck(scheduleItem.scheduleDate()))
                .toList();
        WorkScheduleCalendar calendar = calendarLoader.loadCalendar(command.storeUserId(), loadRanges);

        return schedules.stream()
                .map(scheduleItem -> new AddWorkScheduleCommand(
                        command.storeUserId(),
                        scheduleItem.scheduleDate(),
                        scheduleItem.workTime(),
                        scheduleItem.breakTime(),
                        command.source()
                ))
                .map(addCommand -> calendar.addSchedule(IdGenerator.nextId(), addCommand)
                        .onSuccess(repository::save)
                        .map(WorkSchedule::getId)
                        .mapError(violation -> new FailedAddWorkSchedule(addCommand.scheduleDate(), violation))
                ).toList();
    }

    public record FailedAddWorkSchedule(
            LocalDate scheduleDate,
            WorkScheduleRuleViolation.Create violation
    ) {
    }
}
