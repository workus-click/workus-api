package com.workus.workus.attend.schedule.application.service;

import com.workus.workus.attend.schedule.domain.core.WorkSchedule;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleRepository;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.common.result.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteWorkScheduleService {
    private final WorkScheduleRepository repository;

    public Result<Void, WorkScheduleRuleViolation.Delete> deleteWorkSchedule(Long workScheduleId){
        Optional<WorkSchedule> schedule = repository.findById(workScheduleId);
        if(schedule.isPresent()){
            repository.deleteById(workScheduleId);
            return Result.success(null);
        }else{
            return Result.failure(new WorkScheduleRuleViolation.ScheduleNotFound());
        }
    }
}