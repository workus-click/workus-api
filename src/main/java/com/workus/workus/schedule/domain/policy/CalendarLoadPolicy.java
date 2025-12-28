package com.workus.workus.schedule.domain.policy;

import com.workus.workus.schedule.domain.core.DateRange;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CalendarLoadPolicy {
    private final int EXPAND_DAYS_FOR_CONFLICT_CHECK = 1;

    public DateRange getLoadRangeForConflictCheck(LocalDate scheduleDate) {
        return new DateRange(scheduleDate.minusDays(EXPAND_DAYS_FOR_CONFLICT_CHECK), scheduleDate.plusDays(EXPAND_DAYS_FOR_CONFLICT_CHECK));
    }
}