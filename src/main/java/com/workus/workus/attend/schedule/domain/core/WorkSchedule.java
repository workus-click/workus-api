package com.workus.workus.attend.schedule.domain.core;


import com.workus.workus.attend.common.vo.DateTimeRange;
import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.common.entity.BaseEntity;
import com.workus.workus.attend.schedule.domain.exception.BreakTimeOutOfWorkTimeRangeException;
import com.workus.workus.attend.schedule.domain.exception.WorkScheduleException;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.BreakTimeOutOfWorkTimeRange;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "employee_work_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class WorkSchedule extends BaseEntity {
    @Id @EqualsAndHashCode.Include
    private Long workScheduleId;
    private Long storeUserId;
    @Getter
    private LocalDate scheduleDate;// 근무 시작 날짜 기준
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
    private TimeRange breakTime;       // 휴게 시간 (선택)
    @Embedded
    private WorkScheduleSource source;

    WorkSchedule(
            @NonNull Long workScheduleId,
            @NonNull Long storeUserId,
            @NonNull LocalDate scheduleDate,
            @NonNull TimeRange workTime,
            TimeRange breakTime,
            @NonNull WorkScheduleSource source
    ) {
        if(!isBreakWithinWork(scheduleDate, workTime, breakTime)) {
            throw new WorkScheduleException(new BreakTimeOutOfWorkTimeRange(workTime, breakTime));
        }
        this.workScheduleId = workScheduleId;
        this.storeUserId = storeUserId;
        this.scheduleDate = scheduleDate;
        this.source = source;
        this.workTime = workTime;
        this.breakTime = breakTime;
    }
    public Long getId(){
        return workScheduleId;
    }

    public boolean canChangeWorkTime(TimeRange newWorkTime) {
        Objects.requireNonNull(newWorkTime);
        if(!hasBreakTime()) {
            return true;
        }
        return getBreakDateTime().isWithinRange(DateTimeRange.of(scheduleDate, newWorkTime));
    }
    public void changeWorkTime(TimeRange newWorkTime) {
        if(!canChangeWorkTime(newWorkTime)){
            throw new BreakTimeOutOfWorkTimeRangeException();
        }
        this.workTime = newWorkTime;
    }

    public boolean canChangeBreakTime(TimeRange newBreakTime) {
        if(newBreakTime == null){
            return true;
        }
        return DateTimeRange.of(scheduleDate, newBreakTime).isWithinRange(getWorkDateTime());
    }
    public void changeBreakTime(TimeRange newBreakTime) {
        if(!canChangeBreakTime(newBreakTime)){
            throw new BreakTimeOutOfWorkTimeRangeException();
        }
        this.breakTime = newBreakTime;
    }

    public boolean workTimeOverlaps(WorkSchedule otherSchedule) {
        // [start, end) 기준 겹침 검사
        return this.getWorkDateTime().overlaps(otherSchedule.getWorkDateTime());
    }
    public boolean isOvernightWork() {
        return workTime.spansNextDay();
    }
    public boolean hasBreakTime() {
        return breakTime != null;
    }

    DateTimeRange getWorkDateTime(){
        return DateTimeRange.of(scheduleDate, workTime);
    }
    DateTimeRange getBreakDateTime(){
        if (breakTime == null) {
            return null;
        }
        return DateTimeRange.of(scheduleDate, breakTime);
    }

    static boolean isBreakWithinWork(LocalDate scheduleDate, TimeRange workTime, TimeRange breakTime) {
        if (breakTime == null) {
            return true;
        }
        DateTimeRange workDateTime = DateTimeRange.of(scheduleDate, workTime);
        DateTimeRange breakInterval = DateTimeRange.of(scheduleDate, breakTime);

        return breakInterval.isWithinRange(workDateTime);
    }
}