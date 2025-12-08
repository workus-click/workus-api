package com.workus.workus.schedule.domain.exception;

public class ScheduleConflictException extends RuntimeException{
    public ScheduleConflictException() {
        super("전일/당일/익일 스케줄과 시간이 겹칩니다.");
    }
}
