package com.workus.workus.attend.schedule.util;

import com.workus.workus.attend.common.vo.TimeRange;

import java.time.LocalTime;

public class TimeRanges {
    private TimeRanges(){}
    public static TimeRange parse(String start, String end){
        return new TimeRange(LocalTime.parse(start), LocalTime.parse(end));
    }
}
