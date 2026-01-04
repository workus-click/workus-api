package com.workus.workus.attend.schedule.domain.core;

import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.BreakTimeOutOfWorkTimeRange;
import com.workus.workus.common.result.Result;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class WorkAndBreakTime {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "work_start")),
            @AttributeOverride(name = "end", column = @Column(name = "work_end"))
    })
    private TimeRange workTime;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "break_start")),
            @AttributeOverride(name = "end", column = @Column(name = "break_end"))
    })
    private TimeRange breakTime;

    private WorkAndBreakTime(@NonNull TimeRange workTime, @Nullable TimeRange breakTime) {
        this.workTime = workTime;
        this.breakTime = breakTime;
    }

    /** 휴게시간 포함 근무 */
    public static Result<WorkAndBreakTime, BreakTimeOutOfWorkTimeRange> create(
            TimeRange workTime,
            TimeRange breakTime
    ) {
        return breakTime == null || workTime.contains(breakTime) ? Result.success(new WorkAndBreakTime(workTime, breakTime))
                : Result.failure(new BreakTimeOutOfWorkTimeRange(workTime, breakTime));
    }

    public TimeRange workTime() {
        return workTime;
    }
    public Optional<TimeRange> breakTime() {
        return Optional.ofNullable(breakTime);
    }
}
