package com.workus.workus.attend.schedule.util;

import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.attend.schedule.domain.core.WorkAndBreakTime;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.BreakTimeOutOfWorkTimeRange;
import com.workus.workus.common.result.Result;
import lombok.NonNull;

public class WorkAndBreakTimes {
    private WorkAndBreakTimes() {}

    public static Result<WorkAndBreakTime, BreakTimeOutOfWorkTimeRange> parse(
            @NonNull String workStart,
            @NonNull String workEnd,
            String breakStart,
            String breakEnd
    ) {
        TimeRange workTime = TimeRanges.parse(workStart, workEnd);
        TimeRange breakTime = breakStart == null || breakEnd == null ? null : TimeRanges.parse(breakStart, breakEnd);

        return WorkAndBreakTime.create(workTime, breakTime);
    }
}