package com.workus.workus.schedule.domain.exception;

public class AutoSourceRequiresWorkTimeIdException extends WorkTimeSourcePolicyViolationException {
    public AutoSourceRequiresWorkTimeIdException() {
        super("AUTO source는 workTimeId가 반드시 필요합니다.");
    }
}
