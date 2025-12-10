package com.workus.workus.schedule.domain.exception;

public class ScheduleConflictException extends RuntimeException{
    public ScheduleConflictException(String message) {
        super(message);
    }
}
