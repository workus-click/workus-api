package com.workus.workus.attend.schedule.domain.core;


import com.workus.workus.attend.common.vo.DateTimeRange;
import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.common.entity.BaseEntity;
import com.workus.workus.attend.schedule.domain.exception.WorkScheduleException;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.BreakTimeOutOfWorkTimeRange;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "employee_work_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class WorkSchedule extends BaseEntity {
    @Id @EqualsAndHashCode.Include
    @Column(name = "work_schedule_id")
    private Long id;
    @Getter
    private Long storeUserId;
    @Getter
    private LocalDate scheduleDate;// 근무 시작 날짜 기준
    @Embedded @Getter
    private WorkAndBreakTime workAndBreakTime;
    @Embedded
    private WorkScheduleSource source;

    WorkSchedule(
            @NonNull Long id,
            @NonNull Long storeUserId,
            @NonNull LocalDate scheduleDate,
            @NonNull WorkAndBreakTime workAndBreakTime,
            @NonNull WorkScheduleSource source
    ) {
        this.id = id;
        this.storeUserId = storeUserId;
        this.scheduleDate = scheduleDate;
        this.source = source;
        this.workAndBreakTime = workAndBreakTime;
    }
    public Long getId(){
        return id;
    }

    void changeScheduleDate(@NonNull LocalDate localDate) {
        this.scheduleDate = localDate;
    }
    void changeWorkAndBreakTime(@NonNull WorkAndBreakTime workAndBreakTime){
        this.workAndBreakTime = workAndBreakTime;
    }
    boolean canChangeWorkTime(@NonNull TimeRange newWorkTime) {
        return WorkAndBreakTime.create(newWorkTime, workAndBreakTime.breakTime().orElse(null)).isSuccess();
    }
    void changeWorkTime(TimeRange newWorkTime) {
        if(!canChangeWorkTime(newWorkTime)){
            throw new WorkScheduleException(new BreakTimeOutOfWorkTimeRange(newWorkTime, workAndBreakTime.breakTime().get()));
        }

        this.workAndBreakTime = WorkAndBreakTime.create(
                newWorkTime,
                workAndBreakTime.breakTime().orElse(null)
        ).getOrThrow();
    }

    boolean canChangeBreakTime(TimeRange newBreakTime) {
        return WorkAndBreakTime.create(workAndBreakTime.workTime(), newBreakTime).isSuccess();
    }
    void changeBreakTime(TimeRange newBreakTime) {
        if(!canChangeBreakTime(newBreakTime)){
            throw new WorkScheduleException(new BreakTimeOutOfWorkTimeRange(workAndBreakTime.workTime(), newBreakTime));
        }
        this.workAndBreakTime = WorkAndBreakTime.create(
                workAndBreakTime.workTime(),
                newBreakTime
        ).getOrThrow();
    }

    public boolean workTimeOverlaps(WorkSchedule otherSchedule) {
        // [start, end) 기준 겹침 검사
        return workTimeOverlaps(otherSchedule.scheduleDate, otherSchedule.workAndBreakTime);
    }
    public boolean workTimeOverlaps(LocalDate scheduleDate, WorkAndBreakTime workAndBreakTime) {
        // [start, end) 기준 겹침 검사
        return this.getWorkDateTime().overlaps(DateTimeRange.of(scheduleDate, workAndBreakTime.workTime()));
    }
    public boolean isOvernightWork() {
        return workAndBreakTime.workTime().spansNextDay();
    }
    public boolean hasBreakTime() {
        return workAndBreakTime.breakTime().isPresent();
    }

    DateTimeRange getWorkDateTime(){
        return DateTimeRange.of(scheduleDate, workAndBreakTime.workTime());
    }

}