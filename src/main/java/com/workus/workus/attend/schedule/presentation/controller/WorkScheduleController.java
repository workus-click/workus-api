package com.workus.workus.attend.schedule.presentation.controller;

import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand.ScheduleItem;
import com.workus.workus.attend.schedule.application.service.AddWorkScheduleService;
import com.workus.workus.attend.schedule.domain.core.CreationType;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;
import com.workus.workus.attend.schedule.presentation.controller.dto.AddWorkScheduleRequest;
import com.workus.workus.attend.schedule.presentation.controller.dto.BatchAddWorkScheduleRequest;
import com.workus.workus.attend.schedule.presentation.controller.validation.ValidationSequence;
import com.workus.workus.attend.schedule.util.WorkAndBreakTimes;
import com.workus.workus.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final AddWorkScheduleService addWorkScheduleService;

    @PostMapping
    public ResponseEntity<String> addWorkSchedule(@RequestBody @Validated(ValidationSequence.class) AddWorkScheduleRequest request) {
        AddWorkScheduleCommand command = new AddWorkScheduleCommand(
                request.storeUserId(),
                LocalDate.parse(request.scheduleDate()),
                WorkAndBreakTimes.parse(
                        request.workTimeStart(), request.workTimeEnd(),
                        request.breakTimeStart(), request.breakTimeEnd()
                ).getOrThrow(),
                WorkScheduleSource.ofManual()
        );

        return addWorkScheduleService.addWorkSchedule(command)
                .fold(id -> ResponseEntity.ok(id.toString())
                    , violation ->ResponseEntity.badRequest().body(violation.toString())
                );
    }

    @PostMapping("/batch")
    public ResponseEntity<String> addWorkSchedules(@RequestBody @Validated(ValidationSequence.class) BatchAddWorkScheduleRequest request) {
        Set<ScheduleItem> scheduleItems = request.schedules().stream().map(item -> new ScheduleItem(
                LocalDate.parse(item.scheduleDate()),
                WorkAndBreakTimes.parse(
                        item.workTimeStart(), item.workTimeEnd()
                        , item.breakTimeStart(), item.breakTimeEnd()
                ).getOrThrow()
        )).collect(Collectors.toSet());

        BatchAddWorkSchedulesCommand batchAddWorkSchedulesCommand = new BatchAddWorkSchedulesCommand(
                request.storeUserId(),
                WorkScheduleSource.create(
                        CreationType.valueOf(request.creationType())
                        , request.workTimeId()
                ).getOrThrow(),
                scheduleItems
        );

        List<Result<Long, AddWorkScheduleService.FailedAddWorkSchedule>> results
                = addWorkScheduleService.addWorkSchedules(batchAddWorkSchedulesCommand);

        return ResponseEntity.ok(results.toString());
    }

}