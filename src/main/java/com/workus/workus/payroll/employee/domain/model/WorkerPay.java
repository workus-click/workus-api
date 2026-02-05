package com.workus.workus.payroll.employee.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 노동자 급여 정보 (추상 클래스)
 * - 시급제(HourlyWorkerPay), 월급제(MonthlyWorkerPay) 등 서브클래스로 확장
 * - 급여 형태 변경 시 기존 레코드 삭제 후 새 타입으로 재생성
 */
@Entity
@Table(name = "worker_pay")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pay_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class WorkerPay extends BaseEntity {

    // TODO: 추후 DB에서 관리할 수 있음
    protected static final BigDecimal MINIMUM_HOURLY_RATE = BigDecimal.valueOf(10320);
    protected static final int WEEKS_PER_MONTH = 4;

    @Id
    @Column(name = "worker_pay_id")
    private Long id;

    @Column(name = "store_user_id", updatable = false)
    private Long storeUserId;

    @Column(name = "work_hours_per_day")
    private Integer workHoursPerDay;

    @Column(name = "work_days_per_week")
    private Integer workDaysPerWeek;

    // TODO: 부양가족 수는 연도별로 달라질 수 있음 - 연도별 관리 방안 협의 필요
    @Column(name = "dependents_cnt")
    private Integer dependentsCnt;

    // TODO: 현재 한국인만 타겟으로 KR 고정, 추후 외국인 근로자 지원 시 입력받도록 개선 필요
    // TODO: 필요 시 StoreUser로 이관 검토
    @Enumerated(EnumType.STRING)
    @Column(name = "nationality")
    private Nationality nationality = Nationality.KR;

    /**
     * 월 급여 계산 (서브클래스에서 구현)
     */
    public abstract BigDecimal calculateMonthlySalary();

    /**
     * 시급 계산 (서브클래스에서 구현)
     */
    public abstract BigDecimal calculateHourlyRate();

    /**
     * 급여 형태 반환
     */
    public abstract PayType getPayType();

    protected Long generateId() {
        return IdGenerator.nextId();
    }

    /**
     * 월 총 근무시간 계산
     */
    public int calculateMonthlyWorkHours() {
        if (workHoursPerDay == null || workDaysPerWeek == null) {
            return 0;
        }
        return workHoursPerDay * workDaysPerWeek * WEEKS_PER_MONTH;
    }

    /**
     * 최저시급 검증
     */
    protected void validateMinimumHourlyRate(BigDecimal hourlyRate) {
        if (hourlyRate == null) {
            throw new IllegalArgumentException("시급을 계산할 수 없습니다.");
        }
        if (hourlyRate.compareTo(MINIMUM_HOURLY_RATE) < 0) {
            throw new IllegalArgumentException(
                    String.format("시급은 최저시급(%s원) 이상이어야 합니다. 계산된 시급: %s원",
                            MINIMUM_HOURLY_RATE.toPlainString(), hourlyRate.setScale(0, RoundingMode.FLOOR).toPlainString()));
        }
    }

    public void updateWorkSchedule(Integer workHoursPerDay, Integer workDaysPerWeek) {
        this.workHoursPerDay = workHoursPerDay;
        this.workDaysPerWeek = workDaysPerWeek;
    }

    public void updateDependentsCnt(Integer dependentsCnt) {
        this.dependentsCnt = dependentsCnt;
    }

    public void updateNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    /**
     * 국적 반환 (null인 경우 KR 반환)
     */
    public Nationality getNationality() {
        return nationality != null ? nationality : Nationality.KR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerPay that = (WorkerPay) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
