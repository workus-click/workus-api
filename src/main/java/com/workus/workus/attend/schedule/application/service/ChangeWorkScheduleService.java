package com.workus.workus.attend.schedule.application.service;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.schedule.application.command.ChangeWorkScheduleCommand;
import com.workus.workus.attend.schedule.domain.core.WorkSchedule;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleCalendar;
import com.workus.workus.attend.schedule.domain.policy.CalendarLoadPolicy;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleCalendarLoader;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleRepository;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChangeWorkScheduleService {
    private final CalendarLoadPolicy calendarLoadPolicy;
    private final WorkScheduleCalendarLoader workScheduleCalendarLoader;
    private final WorkScheduleRepository repository;

    public Result<Void, WorkScheduleRuleViolation.Change> changeWorkSchedule(ChangeWorkScheduleCommand command){
        WorkSchedule workSchedule = repository.findById(command.workScheduleId()).get();
        List<DateRange> loadRanges = List.of(
                calendarLoadPolicy.getLoadRangeForConflictCheck(workSchedule.getScheduleDate()),
                calendarLoadPolicy.getLoadRangeForConflictCheck(command.scheduleDate())
        );
        WorkScheduleCalendar workScheduleCalendar = workScheduleCalendarLoader.loadCalendar(workSchedule.getStoreUserId(), loadRanges);

        return Result.success(null);
    }

}
