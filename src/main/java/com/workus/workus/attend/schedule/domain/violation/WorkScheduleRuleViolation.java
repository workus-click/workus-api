package com.workus.workus.attend.schedule.domain.violation;

import com.workus.workus.attend.common.vo.TimeRange;

public sealed interface WorkScheduleRuleViolation
        permits WorkScheduleRuleViolation.CreateAndChange
        , WorkScheduleRuleViolation.Delete
        , WorkScheduleRuleViolation.WorkAndBreakTimeCreation
        , WorkScheduleRuleViolation.WorkTimeSourceCreation {
    // "스케줄 생성"에서만 발생 가능한 위반들
    sealed interface CreateAndChange extends WorkScheduleRuleViolation
            permits ScheduleConflict {}
    sealed interface Delete extends WorkScheduleRuleViolation
            permits ScheduleNotFound {}
    // "스케줄 변경"에서만 발생 가능한 위반들
    sealed interface WorkTimeSourceCreation extends WorkScheduleRuleViolation
            permits AutoSourceMissingWorkTimeId, ManualSourceWithWorkTimeId {}

    sealed interface WorkAndBreakTimeCreation extends WorkScheduleRuleViolation
            permits BreakTimeOutOfWorkTimeRange{}

    // 실제 위반 타입(중앙에서 관리)
    record BreakTimeOutOfWorkTimeRange(TimeRange workTime, TimeRange breakTime)
            implements WorkAndBreakTimeCreation{}
    record ScheduleConflict(Long existingScheduleId)
            implements CreateAndChange {}
    record AutoSourceMissingWorkTimeId() implements WorkTimeSourceCreation{}
    record ManualSourceWithWorkTimeId() implements WorkTimeSourceCreation{}

    record ScheduleNotFound() implements Delete {}
}
