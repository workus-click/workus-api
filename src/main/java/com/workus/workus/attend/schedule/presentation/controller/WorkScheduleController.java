package com.workus.workus.attend.schedule.presentation.controller;

import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand;
import com.workus.workus.attend.schedule.application.command.BatchAddWorkSchedulesCommand.ScheduleItem;
import com.workus.workus.attend.schedule.application.command.ChangeWorkScheduleCommand;
import com.workus.workus.attend.schedule.application.service.AddWorkScheduleService;
import com.workus.workus.attend.schedule.application.service.ChangeWorkScheduleService;
import com.workus.workus.attend.schedule.application.service.DeleteWorkScheduleService;
import com.workus.workus.attend.schedule.domain.core.CreationType;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.ScheduleConflict;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.ScheduleNotFound;
import com.workus.workus.attend.schedule.presentation.controller.dto.AddWorkScheduleRequest;
import com.workus.workus.attend.schedule.presentation.controller.dto.BatchAddWorkScheduleRequest;
import com.workus.workus.attend.schedule.presentation.controller.dto.ChangeWorkScheduleRequest;
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

import static org.springframework.http.HttpStatus.CONFLICT;


@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final AddWorkScheduleService addWorkScheduleService;
    private final ChangeWorkScheduleService changeWorkScheduleService;
    private final DeleteWorkScheduleService deleteWorkScheduleService;

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
                .fold(id -> ResponseEntity.ok(id.toString()), this::mapViolation);
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

    @PutMapping("/{workScheduleId}")
    public ResponseEntity<String> changeWorkSchedule(@PathVariable Long workScheduleId
            , @RequestBody @Validated(ValidationSequence.class) ChangeWorkScheduleRequest request){
        ChangeWorkScheduleCommand command = new ChangeWorkScheduleCommand(
                workScheduleId,
                LocalDate.parse(request.scheduleDate()),
                WorkAndBreakTimes.parse(request.workTimeStart(), request.workTimeEnd(), request.breakTimeStart(), request.breakTimeEnd()).getOrThrow()
        );

        return changeWorkScheduleService.changeWorkSchedule(command)
                .fold(ignored -> ResponseEntity.ok(""), this::mapViolation);

    }

    @DeleteMapping("/{workScheduleId}")
    public ResponseEntity<?> deleteWorkSchedule(@PathVariable Long workScheduleId) {
        return deleteWorkScheduleService.deleteWorkSchedule(workScheduleId)
                .fold(success -> ResponseEntity.noContent().build()
                        , violation -> switch (violation){
                            case ScheduleNotFound scheduleNotFound -> ResponseEntity.notFound().build();
                        }
                );
    }

    private ResponseEntity<String> mapViolation(WorkScheduleRuleViolation.CreateAndChange violation) {
        return switch (violation) {
            case ScheduleConflict scheduleConflict -> ResponseEntity.status(CONFLICT).body(scheduleConflict.toString());
        };
    }
}