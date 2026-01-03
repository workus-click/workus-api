package com.workus.workus.attend.schedule.presentation.controller.dto;

import com.workus.workus.attend.schedule.domain.core.CreationType;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleSource;
import com.workus.workus.attend.schedule.presentation.controller.validation.ValidationRules;
import com.workus.workus.common.presentation.validation.IsoDate;
import com.workus.workus.common.presentation.validation.IsoTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record BatchAddWorkScheduleRequest(
        @NotNull(message = "{schedule.storeUserId.required}")
        Long storeUserId,
        @Valid
        List<ScheduleItem> schedules,
        @NotNull(message = "{schedule.creationType.required}")
        @Pattern(regexp = "MANUAL|AUTO", message = "{schedule.creationType.invalid}")
        String creationType,
        Long workTimeId
) {
    @AssertTrue(message = "{schedule.workTimeId.invalidCreationType}")
    public boolean isValidScheduleSource(){
        return WorkScheduleSource.create(CreationType.valueOf(creationType), workTimeId).isSuccess();
    }
    public record ScheduleItem(
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
        @AssertTrue(message = "{schedule.breakTime.providedAllOrNone}")
        public boolean isValidBreakTime() {
            return ValidationRules.allOrNone(breakTimeStart, breakTimeEnd);
        }
    }

}