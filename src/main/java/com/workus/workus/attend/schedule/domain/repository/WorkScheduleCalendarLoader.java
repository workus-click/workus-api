package com.workus.workus.attend.schedule.domain.repository;

import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleCalendar;

import java.util.List;

public interface WorkScheduleCalendarLoader {
    WorkScheduleCalendar loadCalendar(Long aLong, DateRange loadRange);
    WorkScheduleCalendar loadCalendar(Long aLong, List<DateRange> loadRange);
}
