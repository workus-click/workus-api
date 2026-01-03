package com.workus.workus.attend.schedule.domain.core;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.common.result.Result;
import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.BreakTimeOutOfWorkTimeRange;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.*;

public final class WorkScheduleCalendar {
    private final long storeUserId;
    private final Set<LocalDate> loadedDates;
    private final List<WorkSchedule> schedules;

    private WorkScheduleCalendar(long storeUserId, Set<LocalDate> loadedDates, List<WorkSchedule> loadedSchedules) {
        this.storeUserId = storeUserId;
        this.loadedDates = loadedDates;
        this.schedules = new ArrayList<>(loadedSchedules);
    }

    public static WorkScheduleCalendar of(long storeUserId, List<DateRange> requestedRanges, List<WorkSchedule> loadedSchedules) {
        Set<LocalDate> loadedDates = new HashSet<>();

        for (DateRange requestedRange : requestedRanges) {
            LocalDate localDate = requestedRange.from();
            while (!localDate.isAfter(requestedRange.to())) {
                loadedDates.add(localDate);
                localDate = localDate.plusDays(1);
            }
        }

        return new WorkScheduleCalendar(storeUserId, loadedDates, loadedSchedules);
    }

    public Result<WorkSchedule, WorkScheduleRuleViolation.Create> addSchedule(@NonNull Long workScheduleId, AddWorkScheduleCommand command) {
        if (command.storeUserId() != this.storeUserId) {
            throw new IllegalArgumentException("storeUserId mismatch");
        }
        if (!loadedDates.contains(command.scheduleDate())) {
            throw new IllegalArgumentException("scheduleDate not loaded");
        }

        if(!WorkSchedule.isBreakWithinWork(command.scheduleDate(), command.workTime(), command.breakTime())){
            return Result.failure(new BreakTimeOutOfWorkTimeRange(command.workTime(), command.breakTime()));
        }
        WorkSchedule schedule = new WorkSchedule(
                workScheduleId,
                command.storeUserId(),
                command.scheduleDate(),
                command.workTime(),
                command.breakTime(),
                command.source()
        );

        Result<Void, WorkSchedule> validateConflict = validateConflictSchedule(schedule);
        if (validateConflict.isFailure()) {
            WorkSchedule overlapped = validateConflict.getErrorOrThrow();
            return Result.failure(new WorkScheduleRuleViolation.ScheduleConflict(overlapped.getId()));
        }

        schedules.add(schedule);
        return Result.success(schedule);
    }

    private Result<Void, WorkSchedule> validateConflictSchedule(WorkSchedule candidate) {
        return schedules.stream()
                .filter(existing ->
                        existing.getScheduleDate().equals(candidate.getScheduleDate())
                                || existing.workTimeOverlaps(candidate)
                ).findAny()
                .map(Result::<Void, WorkSchedule>failure)
                .orElse(Result.success(null));
    }
}