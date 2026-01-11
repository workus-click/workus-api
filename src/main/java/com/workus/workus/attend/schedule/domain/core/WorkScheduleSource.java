package com.workus.workus.attend.schedule.domain.core;

import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.WorkTimeSourceCreation;
import com.workus.workus.common.result.Result;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class WorkScheduleSource {
    @Enumerated(EnumType.STRING)
    private CreationType creationType;
    private Long workTimeId;

    private WorkScheduleSource(@NonNull CreationType creationType, Long workTimeId) {
        this.creationType = creationType;
        this.workTimeId = workTimeId;
    }
    public static Result<WorkScheduleSource, WorkTimeSourceCreation> create(@NonNull CreationType creationType, Long workTimeId){
        if (creationType == CreationType.AUTO && workTimeId == null) {
            return Result.failure(new WorkScheduleRuleViolation.AutoSourceMissingWorkTimeId());
        }
        if (creationType == CreationType.MANUAL && workTimeId != null) {
            return Result.failure(new WorkScheduleRuleViolation.ManualSourceWithWorkTimeId());
        }
        return Result.success(new WorkScheduleSource(creationType, workTimeId));
    }
    public static WorkScheduleSource ofAuto(@NonNull Long workTimeId) {
        return new WorkScheduleSource(CreationType.AUTO, workTimeId);
    }
    public static WorkScheduleSource ofManual() {
        return new WorkScheduleSource(CreationType.MANUAL, null);
    }
    public CreationType creationType() { return creationType; }
    public Long workTimeId() { return workTimeId; }
}