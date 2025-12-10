package com.workus.workus.schedule.presentation.controller;

import com.workus.workus.schedule.application.service.CreateWorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final CreateWorkScheduleService createWorkScheduleService;
}