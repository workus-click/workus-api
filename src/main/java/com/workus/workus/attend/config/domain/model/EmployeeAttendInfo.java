package com.workus.workus.attend.config.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.util.Objects;

@Entity
@Table(name = "employee_attend_info",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_store_user_day_of_week",
           columnNames = {"store_user_id", "day_of_week"}
       ))
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeAttendInfo extends BaseEntity {
    @Id
    @Column(name = "employee_attend_info_id")
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long storeUserId;

    // 요일제에서 일자 단위로 근무스케줄 편성 예정이 없으므로 workdays -> day_of_week으로 변경
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3, updatable = false)
    private DayOfWeek dayOfWeek;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_time_id", nullable = false)
    private WorkTimeConfig workTimeConfig;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public static EmployeeAttendInfo of(
            Long storeUserId,
            DayOfWeek dayOfWeek,
            WorkTimeConfig workTimeConfig) {
        return EmployeeAttendInfo.builder()
                .id(IdGenerator.nextId())
                .storeUserId(storeUserId)
                .dayOfWeek(dayOfWeek)
                .workTimeConfig(workTimeConfig)
                .build();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public void changeWorkTimeConfig(WorkTimeConfig workTimeConfig) {
        this.workTimeConfig = workTimeConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeAttendInfo that = (EmployeeAttendInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

