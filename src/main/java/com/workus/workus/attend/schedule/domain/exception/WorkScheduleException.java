package com.workus.workus.attend.schedule.domain.exception;

import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;

public class WorkScheduleException extends RuntimeException {
    private WorkScheduleRuleViolation violation;
    public WorkScheduleException(WorkScheduleRuleViolation violation) {
        this.violation = violation;
    }
}
