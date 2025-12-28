package com.workus.workus.schedule.domain.violation;

import com.workus.workus.schedule.domain.core.TimeRange;

public sealed interface WorkScheduleRuleViolation
        permits WorkScheduleRuleViolation.Create,
        WorkScheduleRuleViolation.ChangeWorkTime {
    // "스케줄 생성"에서만 발생 가능한 위반들
    sealed interface Create extends WorkScheduleRuleViolation
            permits BreakTimeOutOfWorkTimeRange, ScheduleConflict {}
    // "근무시간 변경"에서만 발생 가능한 위반들
    sealed interface ChangeWorkTime extends WorkScheduleRuleViolation
            permits BreakTimeOutOfWorkTimeRange {}


    // 실제 위반 타입(중앙에서 관리)
    record BreakTimeOutOfWorkTimeRange(TimeRange workTime, TimeRange breakTime)
            implements Create, ChangeWorkTime {}

    record ScheduleConflict(Long existingScheduleId)
            implements Create {}
}
