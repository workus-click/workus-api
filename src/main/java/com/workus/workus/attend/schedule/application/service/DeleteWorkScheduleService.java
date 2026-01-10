package com.workus.workus.attend.schedule.application.service;

import com.workus.workus.attend.schedule.domain.repository.WorkScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteWorkScheduleService {
    private final WorkScheduleRepository repository;

    public void deleteWorkSchedule(Long workScheduleId){
        repository.deleteById(workScheduleId);
    }
}