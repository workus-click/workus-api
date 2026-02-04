package com.workus.workus.payroll.dependents.domain.model;

/**
 * TODO: 삭제 예정
 * - 현재 프론트에 미반영 상태
 * - 설계 의도: 다음 년도 졸업, 나이 변경 등으로 부양가족 자격이 달라지는 경우를 트래킹하기 위해 포함
 * - 현재는 WorkerPay.dependentsCnt로 단순 관리 중
 * - 프론트 반영 계획이 없어 삭제 예정
 */

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Entity
@Table(name = "dependent")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dependent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependent_id")
    private Long id;

    @Column(name = "store_user_id", nullable = false, updatable = false)
    private Long storeUserId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false, length = 20)
    private Relationship relationship;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_student", nullable = false)
    private boolean isStudent = false;

    @Column(name = "is_disabled", nullable = false)
    private boolean isDisabled = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public static Dependent of(
            Long storeUserId,
            String name,
            Relationship relationship,
            LocalDate birthDate,
            boolean isStudent,
            boolean isDisabled) {
        return Dependent.builder()
                .id(IdGenerator.nextId())
                .storeUserId(storeUserId)
                .name(name)
                .relationship(relationship)
                .birthDate(birthDate)
                .isStudent(isStudent)
                .isDisabled(isDisabled)
                .isDeleted(false)
                .build();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void changeBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isChild() {
        return this.relationship == Relationship.CHILD;
    }

    /**
     * 법적으로 부양가족으로 인정되는 자녀인지 확인
     * - 일반 자녀: 만 20세 미만
     * - 대학생 자녀: 만 25세 미만 (추후 isStudent 필드 추가 시 확장 가능)
     * - 장애인 자녀: 나이 제한 없음 (추후 isDisabled 필드 추가 시 확장 가능)
     */
    public boolean isEligibleChild() {
        if (!isChild() || this.birthDate == null) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        int age = Period.between(this.birthDate, today).getYears();

        // 장애인여부 확인

        // 대학생인 경우 만 20세 미만인 경우 부양가족으로 인정
        if(isStudent)
            return age < 25;

        // 대학생도 아니고 장애인도 아닌 자녀는 20세 이하만 부양가족으로 인정
        return age < 20;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependent dependent = (Dependent) o;
        return Objects.equals(id, dependent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


