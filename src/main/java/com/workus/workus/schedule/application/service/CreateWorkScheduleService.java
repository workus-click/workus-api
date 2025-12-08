package com.workus.workus.schedule.application.service;

import com.workus.workus.schedule.application.command.CreateWorkScheduleCommand;
import com.workus.workus.schedule.domain.repository.WorkScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateWorkScheduleService {
    private final WorkScheduleRepository repository;

    public void createWorkSchedule(CreateWorkScheduleCommand command) {

    }
}
