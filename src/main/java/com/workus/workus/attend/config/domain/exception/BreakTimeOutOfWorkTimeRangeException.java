package com.workus.workus.attend.config.domain.exception;

public class BreakTimeOutOfWorkTimeRangeException extends RuntimeException {
    public BreakTimeOutOfWorkTimeRangeException() {
        super("휴게시간은 근무시간 범위 안이어야 합니다.");
    }
}

