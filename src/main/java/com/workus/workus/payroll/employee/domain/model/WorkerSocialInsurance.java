package com.workus.workus.payroll.employee.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

/**
 * 노동자 사회보험 가입 정보
 * - 보험 종류별로 레코드 관리
 * - 추후 가입일, 요율 등 추가 정보 확장 가능
 */
@Entity
@Table(name = "worker_social_insurance",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_store_user_insurance_type",
           columnNames = {"store_user_id", "insurance_type"}
       ))
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkerSocialInsurance extends BaseEntity {

    @Id
    @Column(name = "worker_social_insurance_id")
    private Long id;

    @Column(name = "store_user_id", updatable = false)
    private Long storeUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", updatable = false)
    private SocialInsuranceType insuranceType;

    @Column(name = "enrolled")
    private Boolean enrolled;

    // TODO: 추후 확장 가능한 필드들
    // private LocalDate enrollmentDate;  // 가입일
    // private LocalDate withdrawalDate;  // 탈퇴일
    // private BigDecimal rate;           // 요율

    public static WorkerSocialInsurance of(
            Long storeUserId,
            SocialInsuranceType insuranceType,
            Boolean enrolled) {
        return WorkerSocialInsurance.builder()
                .id(IdGenerator.nextId())
                .storeUserId(storeUserId)
                .insuranceType(insuranceType)
                .enrolled(enrolled)
                .build();
    }

    public void updateEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isEnrolled() {
        return Boolean.TRUE.equals(enrolled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerSocialInsurance that = (WorkerSocialInsurance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
