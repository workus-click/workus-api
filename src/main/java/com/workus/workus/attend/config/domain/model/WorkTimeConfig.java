package com.workus.workus.attend.config.domain.model;

import com.workus.workus.attend.config.domain.exception.BreakTimeOutOfWorkTimeRangeException;
import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import com.workus.workus.schedule.domain.model.TimeRange;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@Table(name = "work_time_config")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkTimeConfig extends BaseEntity {
    @Id
    @Column(name = "work_time_id")
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long storeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private EmployeeType workerType;

    @Column(name = "work_time_name", nullable = false)
    private String title;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "start", column = @Column(name = "start_time", nullable = false)),
        @AttributeOverride(name = "end", column = @Column(name = "end_time", nullable = false))
    })
    private TimeRange workTime;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "start", column = @Column(name = "break_start_time")),
        @AttributeOverride(name = "end", column = @Column(name = "break_end_time"))
    })
    private TimeRange breakTime;

    @Column
    private boolean isDeleted;

    /**
     * 정적 팩토리 메서드: 근무시간 설정 생성
     */
    public static WorkTimeConfig of(
            Long storeId,
            EmployeeType workerType,
            String title,
            TimeRange workTime,
            TimeRange breakTime) {
        WorkTimeConfig config = WorkTimeConfig.builder()
                .id(IdGenerator.nextId())
                .storeId(storeId)
                .workerType(workerType)
                .title(title)
                .workTime(workTime)
                .breakTime(breakTime)
                .build();

        config.validateBreakWithinWork();

        return config;
    }

    /**
     * 근무시간 변경 가능 여부 확인.
     * 
     * @param workTime 새로운 근무시간
     * @return 변경 가능 여부
     */
    public boolean canChangeWorkTime(TimeRange workTime) {
        Objects.requireNonNull(workTime);
        return isBreakTimeWithinWorkTime(workTime, this.breakTime);
    }

    /**
     * 근무시간을 변경
     * 
     * @param workTime 새로운 근무시간
     * @throws BreakTimeOutOfWorkTimeRangeException 휴게시간이 새로운 근무시간 범위를 벗어나는 경우
     */
    public void changeWorkTime(TimeRange workTime) {
        if (!canChangeWorkTime(workTime))
            throw new BreakTimeOutOfWorkTimeRangeException();
        this.workTime = workTime;
    }

    /**
     * 휴게시간 변경 가능 여부를 확인.
     * 
     * @param newBreakTime 새로운 휴게시간
     * @return 변경 가능 여부
     */
    public boolean canChangeBreakTime(TimeRange newBreakTime) {
        Objects.requireNonNull(newBreakTime);
        return isBreakTimeWithinWorkTime(this.workTime, newBreakTime);
    }

    /**
     * 휴게시간을 변경합니다.
     * 
     * @param newBreakTime 새로운 휴게시간
     * @throws BreakTimeOutOfWorkTimeRangeException 휴게시간이 근무시간 범위를 벗어나는 경우
     */
    public void changeBreakTime(TimeRange newBreakTime) {
        if (!canChangeBreakTime(newBreakTime))
            throw new BreakTimeOutOfWorkTimeRangeException();

        this.breakTime = newBreakTime;
    }

    public boolean isNextDay() {
        return workTime.spansNextDay();
    }

    /**
     * 휴게시간이 근무시간 범위 안에 있는지 검증.
     * 
     * @throws BreakTimeOutOfWorkTimeRangeException 휴게시간이 근무시간 범위를 벗어나는 경우
     */
    private void validateBreakWithinWork() {
        if (!isBreakTimeWithinWorkTime(this.workTime, this.breakTime))
            throw new BreakTimeOutOfWorkTimeRangeException();
    }

    /**
     * 휴게시간이 근무시간 범위 안에 있는지 확인.
     * 
     * @param workTime 근무시간
     * @param breakTime 휴게시간 (null일 수 있음)
     * @return 휴게시간이 null이거나 근무시간 범위 안에 있으면 true
     */
    private boolean isBreakTimeWithinWorkTime(TimeRange workTime, TimeRange breakTime) {
        Objects.requireNonNull(workTime);
        
        // 휴게시간이 null이면 항상 true 반환 (휴게시간은 선택사항)
        if (breakTime == null)
            return true;

        boolean workSpansNextDay = workTime.spansNextDay();
        boolean breakSpansNextDay = breakTime.spansNextDay();

        // 근무시간이 다음날 넘어가지 않는 경우
        if (!workSpansNextDay) {
            // 휴게시간도 다음날 넘어가지 않아야 하고, 근무시간 범위 안에 있어야 함
            if (breakSpansNextDay)
                return false;

            return breakTime.start().compareTo(workTime.start()) >= 0
                    && breakTime.end().compareTo(workTime.end()) <= 0;
        }

        // 근무시간이 다음날 넘어가는 경우
        // 휴게시간이 다음날 넘어가지 않는 경우: 휴게시간이 근무시간 시작 이후이거나 근무시간 종료 이전이어야 함
        if (!breakSpansNextDay)
            return breakTime.start().compareTo(workTime.start()) >= 0
                    || breakTime.end().compareTo(workTime.end()) <= 0;

        // 둘 다 다음날 넘어가는 경우: 휴게시간이 근무시간 범위 안에 있어야 함
        // 시작 시간 비교
        int startCompare = breakTime.start().compareTo(workTime.start());
        // 종료 시간 비교
        int endCompare = breakTime.end().compareTo(workTime.end());

        // 시작 시간이 같거나 이후이고, 종료 시간이 같거나 이전이어야 함
        return startCompare >= 0 && endCompare <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTimeConfig that = (WorkTimeConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

