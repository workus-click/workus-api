package com.workus.workus.attend.schedule.domain.repository;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleCalendar;

import java.util.Collection;

public interface WorkScheduleCalendarLoader {
    WorkScheduleCalendar loadCalendar(Long aLong, DateRange loadRange);
    WorkScheduleCalendar loadCalendar(Long aLong, Collection<DateRange> loadRange);
}
