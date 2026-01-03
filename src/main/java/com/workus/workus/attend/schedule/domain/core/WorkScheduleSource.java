package com.workus.workus.attend.schedule.domain.core;

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
        if (creationType == CreationType.AUTO && workTimeId == null) {
            throw new IllegalArgumentException("WorkTimeId must be provided for AUTO creation type");
        }
        if (creationType == CreationType.MANUAL && workTimeId != null) {
            throw new IllegalArgumentException("WorkTimeId must be null for MANUAL creation type");
        }
        this.creationType = creationType;
        this.workTimeId = workTimeId;
    }

    public static WorkScheduleSource ofAuto(Long workTimeId) {
        return new WorkScheduleSource(CreationType.AUTO, workTimeId);
    }
    public static WorkScheduleSource ofManual() {
        return new WorkScheduleSource(CreationType.MANUAL, null);
    }
    public CreationType creationType() { return creationType; }
    public Long workTimeId() { return workTimeId; }
}