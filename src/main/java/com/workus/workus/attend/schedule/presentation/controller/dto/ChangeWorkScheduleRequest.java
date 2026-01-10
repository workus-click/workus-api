package com.workus.workus.attend.schedule.presentation.controller.dto;

import com.workus.workus.attend.schedule.presentation.controller.validation.ValidationRules;
import com.workus.workus.attend.schedule.util.WorkAndBreakTimes;
import com.workus.workus.common.presentation.validation.IsoDate;
import com.workus.workus.common.presentation.validation.IsoTime;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record ChangeWorkScheduleRequest(
    @NotBlank(message = "{schedule.scheduleDate.required}")
    @IsoDate
    String scheduleDate,
    @NotBlank(message = "{schedule.workTimeStart.required}")
    @IsoTime
    String workTimeStart,
    @NotBlank(message = "{schedule.workTimeEnd.required}")
    @IsoTime
    String workTimeEnd,
    @IsoTime
    String breakTimeStart,
    @IsoTime
    String breakTimeEnd
) {
    @AssertTrue(message = "{schedule.breakTime.incompleteRange}")
    public boolean isValidBreakTime() {
        return ValidationRules.allOrNone(breakTimeStart, breakTimeEnd);
    }
    @AssertTrue(message = "{schedule.breakTime.outOfWorkTimeRange}")
    public boolean isBreakTimeWithinWorkTime(){
        return WorkAndBreakTimes.parse(workTimeStart, workTimeEnd, breakTimeStart, breakTimeEnd).isSuccess();
    }
}