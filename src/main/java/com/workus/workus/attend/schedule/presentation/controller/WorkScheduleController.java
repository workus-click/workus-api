package com.workus.workus.attend.schedule.presentation.controller;

import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand.ScheduleItem;
import com.workus.workus.attend.schedule.application.service.AddWorkScheduleService;
import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.attend.schedule.domain.core.CreationType;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.attend.schedule.presentation.controller.dto.AddWorkScheduleRequest;
import com.workus.workus.attend.schedule.presentation.controller.dto.BatchAddWorkScheduleRequest;
import com.workus.workus.common.presentation.dto.Response;
import com.workus.workus.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.workus.workus.attend.schedule.domain.core.CreationType.AUTO;


@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final AddWorkScheduleService addWorkScheduleService;

    @PostMapping
    public ResponseEntity<String> addWorkSchedule(@RequestBody @Valid AddWorkScheduleRequest request) {
        if(request.breakTimeStart() == null && request.breakTimeEnd() != null
        || request.breakTimeStart() != null && request.breakTimeEnd() == null) {
            return ResponseEntity.badRequest().body("Both breakTimeStart and breakTimeEnd should be provided together or not at all.");
        }
        TimeRange workTimeRange = new TimeRange(LocalTime.parse(request.workTimeStart()), LocalTime.parse(request.workTimeEnd()));
        TimeRange breakTimeRange = request.breakTimeStart() == null ? null
                : new TimeRange(LocalTime.parse(request.breakTimeStart()), LocalTime.parse(request.breakTimeEnd()));

        AddWorkScheduleCommand command = new AddWorkScheduleCommand(
                request.storeUserId(),
                LocalDate.parse(request.scheduleDate()),
                workTimeRange,
                breakTimeRange,
                WorkScheduleSource.ofManual()
        );

        return addWorkScheduleService.addWorkSchedule(command)
                .fold(id -> ResponseEntity.ok(id.toString())
                    , violation -> switch (violation) {
                        case WorkScheduleRuleViolation.BreakTimeOutOfWorkTimeRange breakTimeOutOfWorkTimeRange ->
                                ResponseEntity.badRequest().body(breakTimeOutOfWorkTimeRange.toString());
                        case WorkScheduleRuleViolation.ScheduleConflict scheduleConflict ->
                                ResponseEntity.badRequest().body(scheduleConflict.toString());
                    }
                );
    }

    @PostMapping("/batch")
    public ResponseEntity<String> addWorkSchedules(@RequestBody @Valid BatchAddWorkScheduleRequest request) {
        CreationType creationType = CreationType.valueOf(request.creationType());
        Set<ScheduleItem> scheduleItems = request.schedules().stream().map(item -> new ScheduleItem(
                LocalDate.parse(item.scheduleDate()),
                new TimeRange(LocalTime.parse(item.workTimeStart()), LocalTime.parse(item.workTimeEnd())),
                item.breakTimeStart() == null ? null
                        : new TimeRange(LocalTime.parse(item.breakTimeStart()), LocalTime.parse(item.breakTimeEnd()))
        )).collect(Collectors.toSet());

        BatchAddWorkSchedulesCommand batchAddWorkSchedulesCommand = new BatchAddWorkSchedulesCommand(
                request.storeUserId(),
                creationType == AUTO ? WorkScheduleSource.ofAuto(request.workTimeId()) : WorkScheduleSource.ofManual(),
                scheduleItems
        );
        List<Result<Long, AddWorkScheduleService.FailedAddWorkSchedule>> results = addWorkScheduleService.addWorkSchedules(batchAddWorkSchedulesCommand);

        return ResponseEntity.ok(results.toString());
    }

}