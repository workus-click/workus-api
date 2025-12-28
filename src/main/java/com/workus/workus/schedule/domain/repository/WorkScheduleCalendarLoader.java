package com.workus.workus.schedule.domain.repository;

import com.workus.workus.schedule.domain.core.DateRange;
import com.workus.workus.schedule.domain.core.WorkScheduleCalendar;

import java.util.List;

public interface WorkScheduleCalendarLoader {
    WorkScheduleCalendar loadCalendar(Long aLong, DateRange loadRange);
    WorkScheduleCalendar loadCalendar(Long aLong, List<DateRange> loadRange);
}
