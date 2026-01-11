package com.workus.workus.attend.schedule.domain.exception;

import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import lombok.Getter;

public class WorkScheduleException extends RuntimeException{
    @Getter
    private final WorkScheduleRuleViolation violation;
    public WorkScheduleException(WorkScheduleRuleViolation violation) {
        this.violation = violation;
    }
}