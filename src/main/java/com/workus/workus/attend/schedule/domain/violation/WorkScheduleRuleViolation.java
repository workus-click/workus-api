package com.workus.workus.attend.schedule.domain.violation;

import com.workus.workus.attend.common.vo.TimeRange;

public sealed interface WorkScheduleRuleViolation
        permits WorkScheduleRuleViolation.Create,
        WorkScheduleRuleViolation.Change,
        WorkScheduleRuleViolation.WorkTimeSourceCreation,
        WorkScheduleRuleViolation.WorkAndBreakTimeCreation {
    // "스케줄 생성"에서만 발생 가능한 위반들
    sealed interface Create extends WorkScheduleRuleViolation
            permits ScheduleConflict {}
    // "스케줄 변경"에서만 발생 가능한 위반들
    sealed interface Change extends WorkScheduleRuleViolation
            permits ScheduleConflict{}
    sealed interface WorkTimeSourceCreation extends WorkScheduleRuleViolation
            permits AutoSourceMissingWorkTimeId, ManualSourceWithWorkTimeId {}

    sealed interface WorkAndBreakTimeCreation extends WorkScheduleRuleViolation
            permits BreakTimeOutOfWorkTimeRange{}

    // 실제 위반 타입(중앙에서 관리)
    record BreakTimeOutOfWorkTimeRange(TimeRange workTime, TimeRange breakTime)
            implements WorkAndBreakTimeCreation{}
    record ScheduleConflict(Long existingScheduleId)
            implements Create, Change {}
    record AutoSourceMissingWorkTimeId() implements WorkTimeSourceCreation{}
    record ManualSourceWithWorkTimeId() implements WorkTimeSourceCreation{}

}
