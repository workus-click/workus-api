package com.workus.workus.attend.schedule.presentation.dto;

import com.workus.workus.attend.schedule.presentation.validation.*;
import com.workus.workus.attend.schedule.util.WorkAndBreakTimes;
import com.workus.workus.common.presentation.validation.IsoTime;

public record ChangeWorkScheduleRequest(
    @ScheduleDateRequired
    String scheduleDate,
    @WorkTimeStartRequired
    String workTimeStart,
    @WorkTimeEndRequired
    String workTimeEnd,
    @IsoTime
    String breakTimeStart,
    @IsoTime
    String breakTimeEnd
) {
    @CompleteBreakTimeRange(groups = {AfterDefault.class})
    public boolean isValidBreakTime() {
        return ValidationRules.allOrNone(breakTimeStart, breakTimeEnd);
    }
    @BreakTimeWithinWorkTime(groups = {AfterDefault.class})
    public boolean isBreakTimeWithinWorkTime(){
        return WorkAndBreakTimes.parse(workTimeStart, workTimeEnd, breakTimeStart, breakTimeEnd).isSuccess();
    }
}