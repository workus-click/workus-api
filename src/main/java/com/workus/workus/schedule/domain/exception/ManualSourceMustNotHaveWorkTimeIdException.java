package com.workus.workus.schedule.domain.exception;

public class ManualSourceMustNotHaveWorkTimeIdException extends WorkTimeSourcePolicyViolationException{
    public ManualSourceMustNotHaveWorkTimeIdException() {
        super("MANUAL source에서는 workTimeId가 존재하면 안 됩니다.");
    }
}
