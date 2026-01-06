package com.workus.workus.attend.schedule.domain.core;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.common.result.Result;
import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.*;

public final class WorkScheduleCalendar {
    private final long storeUserId;
    private final Set<LocalDate> loadedDates;
    private final Set<WorkSchedule> schedules;

    private WorkScheduleCalendar(long storeUserId, Set<LocalDate> loadedDates, Collection<WorkSchedule> loadedSchedules) {
        if(loadedSchedules.stream()
                .map(WorkSchedule::getStoreUserId)
                .anyMatch(userId -> !userId.equals(storeUserId))){
            throw new IllegalArgumentException("storeUserId가 다른 스케줄이 포함되어있습니다.");
        }

        this.storeUserId = storeUserId;
        this.loadedDates = loadedDates;
        this.schedules = new HashSet<>(loadedSchedules);
    }

    public static WorkScheduleCalendar of(long storeUserId, Collection<DateRange> requestedRanges, Collection<WorkSchedule> loadedSchedules) {
        Set<LocalDate> loadedDates = new HashSet<>();

        for (DateRange requestedRange : requestedRanges) {
            LocalDate localDate = requestedRange.start();
            while (localDate.isBefore(requestedRange.end())) {
                loadedDates.add(localDate);
                localDate = localDate.plusDays(1);
            }
        }

        return new WorkScheduleCalendar(storeUserId, loadedDates, loadedSchedules);
    }

    public Result<WorkSchedule, WorkScheduleRuleViolation.CreateAndChange> addSchedule(@NonNull Long workScheduleId, AddWorkScheduleCommand command) {
        if (!command.storeUserId().equals(this.storeUserId)) {
            throw new IllegalArgumentException("storeUserId mismatch");
        }
        if (!loadedDates.contains(command.scheduleDate())) {
            throw new IllegalArgumentException("scheduleDate not loaded");
        }

        WorkSchedule schedule = new WorkSchedule(
                workScheduleId,
                command.storeUserId(),
                command.scheduleDate(),
                command.workAndBreakTime(),
                command.source()
        );

        Result<Void, WorkSchedule> validateConflict = validateNoConflict(workScheduleId, schedule.getScheduleDate(), schedule.getWorkAndBreakTime());
        if (validateConflict.isFailure()) {
            WorkSchedule overlapped = validateConflict.getErrorOrThrow();
            return Result.failure(new WorkScheduleRuleViolation.ScheduleConflict(overlapped.getId()));
        }

        schedules.add(schedule);
        return Result.success(schedule);
    }

    public Result<Void, WorkScheduleRuleViolation.CreateAndChange> changeSchedule(WorkSchedule workSchedule, LocalDate newDate, WorkAndBreakTime newTime) {
        if (!workSchedule.getStoreUserId().equals(this.storeUserId)) {
            throw new IllegalArgumentException("storeUserId mismatch");
        }
        if (!loadedDates.contains(newDate)) {
            throw new IllegalArgumentException("scheduleDate not loaded");
        }

        return validateNoConflict(workSchedule.getId(), newDate, newTime)
                .onSuccess(ignored -> {
                    workSchedule.changeScheduleDate(newDate);
                    workSchedule.changeWorkAndBreakTime(newTime);
                    schedules.add(workSchedule);
                })
                .mapError(existing -> new WorkScheduleRuleViolation.ScheduleConflict(existing.getId()));
    }
    private Result<Void, WorkSchedule> validateNoConflict(Long id, LocalDate scheduleDate, WorkAndBreakTime workAndBreakTime) {
        return schedules.stream()
                .filter(existing -> !existing.getId().equals(id)
                        && (existing.getScheduleDate().equals(scheduleDate) || existing.workTimeOverlaps(scheduleDate, workAndBreakTime))
                ).findAny()
                .map(Result::<Void, WorkSchedule>failure)
                .orElse(Result.success(null));
    }
}