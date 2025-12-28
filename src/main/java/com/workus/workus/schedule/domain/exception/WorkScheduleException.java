package com.workus.workus.schedule.domain.exception;

import com.workus.workus.schedule.domain.violation.WorkScheduleRuleViolation;

public class WorkScheduleException extends RuntimeException {
    private WorkScheduleRuleViolation violation;
    public WorkScheduleException(WorkScheduleRuleViolation violation) {
        this.violation = violation;
    }
}
