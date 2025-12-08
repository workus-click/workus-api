package com.workus.workus.schedule.domain.model;


import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import com.workus.workus.schedule.domain.exception.BreakTimeOutOfWorkTimeRangeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private WorkScheduleSource source;    // AUTO, USER
    private Long workTimeId;          // nullable

    public WorkSchedule(
            Long storeUserId,
            LocalDate scheduleDate,
            TimeRange workTime,
            TimeRange breakTime,
            WorkScheduleSource source,
            Long workTimeId
    ) {
        this.workScheduleId = IdGenerator.nextId();
        this.storeUserId = Objects.requireNonNull(storeUserId);
        this.scheduleDate = Objects.requireNonNull(scheduleDate);
        this.source = Objects.requireNonNull(source);
        this.workTime = Objects.requireNonNull(workTime);
        this.breakTime = breakTime;
        this.workTimeId = workTimeId;

        validateBreakWithinWork();
    }

    public Long getId(){
        return workScheduleId;
    }

    public void changeWorkTime(TimeRange newWorkTime) {
        if(!hasBreakTime() || getBreakDateTime().isWithinRange(getDateTime(newWorkTime))){
            setWorkTime(newWorkTime);
        }
    }
    public boolean workTimeOverlaps(WorkSchedule otherSchedule) {
        // [start, end) 기준 겹침 검사
        return !this.getWorkDateTime().overlaps(otherSchedule.getWorkDateTime());
    }
    public boolean isOvernightWork() {;
        return workTime.spansNextDay();
    }
    public boolean hasBreakTime() {
        return breakTime != null;
    }

    DateTimeRange getWorkDateTime(){
        return getDateTime(workTime);
    }
    DateTimeRange getBreakDateTime(){
        if (breakTime == null) {
            return null;
        }
        return getDateTime(breakTime);
    }
    DateTimeRange getDateTime(TimeRange timeRange){
        return new DateTimeRange(
                LocalDateTime.of(scheduleDate, timeRange.start())
                , LocalDateTime.of(timeRange.spansNextDay()? scheduleDate.plusDays(1) : scheduleDate, timeRange.end())
        );
    }

    private void validateBreakWithinWork() {
        if (breakTime == null) {
            return;
        }
        DateTimeRange workDateTime = getWorkDateTime();
        DateTimeRange breakInterval = getBreakDateTime();

        if(!breakInterval.isWithinRange(workDateTime)){
            throw new BreakTimeOutOfWorkTimeRangeException();
        }
    }
    private void setWorkTime(TimeRange newWorkTime){
        Objects.requireNonNull(newWorkTime);
        this.workTime = newWorkTime;
    }
}