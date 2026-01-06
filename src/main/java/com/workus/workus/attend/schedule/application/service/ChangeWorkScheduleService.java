package com.workus.workus.attend.schedule.application.service;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.schedule.application.command.ChangeWorkScheduleCommand;
import com.workus.workus.attend.schedule.domain.core.WorkSchedule;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleCalendar;
import com.workus.workus.attend.schedule.domain.policy.CalendarLoadPolicy;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleCalendarLoader;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleRepository;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.common.exception.ResourceNotFoundException;
import com.workus.workus.common.result.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.workus.workus.common.constant.ResourceType.WORK_SCHEDULE;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeWorkScheduleService {
    private final CalendarLoadPolicy calendarLoadPolicy;
    private final WorkScheduleCalendarLoader workScheduleCalendarLoader;
    private final WorkScheduleRepository repository;

    public Result<Void, WorkScheduleRuleViolation.CreateAndChange> changeWorkSchedule(ChangeWorkScheduleCommand command){
        WorkSchedule workSchedule = repository.findById(command.workScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(WORK_SCHEDULE, command.workScheduleId()));

        DateRange loadRange = calendarLoadPolicy.getLoadRange(command.scheduleDate());
        WorkScheduleCalendar calendar = workScheduleCalendarLoader.loadCalendar(workSchedule.getStoreUserId(), loadRange);

        return calendar.changeSchedule(workSchedule, command.scheduleDate(), command.workAndBreakTime());
    }

}