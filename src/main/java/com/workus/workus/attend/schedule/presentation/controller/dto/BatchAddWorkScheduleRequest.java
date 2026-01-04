package com.workus.workus.attend.schedule.presentation.controller.dto;

import com.workus.workus.attend.schedule.domain.core.CreationType;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;
import com.workus.workus.attend.schedule.presentation.controller.validation.*;
import com.workus.workus.attend.schedule.util.WorkAndBreakTimes;
import com.workus.workus.common.presentation.validation.IsoTime;
import jakarta.validation.Valid;

import java.util.List;

public record BatchAddWorkScheduleRequest(
        @StoreUserIdRequired
        Long storeUserId,
        @Valid
        List<ScheduleItem> schedules,
        @ValidCreationType
        String creationType,
        Long workTimeId
) {
    @MatchingCreationTypeAndWorkTimeId(groups = {AfterDefault.class})
    public boolean isValidScheduleSource(){
        return WorkScheduleSource.create(CreationType.valueOf(creationType), workTimeId).isSuccess();
    }
    public record ScheduleItem(
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
}