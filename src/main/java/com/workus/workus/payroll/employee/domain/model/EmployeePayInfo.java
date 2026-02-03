package com.workus.workus.payroll.employee.domain.model;

import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "employee_pay_info",
        uniqueConstraints = @UniqueConstraint(
        name = "uk_store_user_pay_type",
        columnNames = {"store_user_id", "pay_type_code_id"}
))
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeePayInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_user_id", nullable = false, updatable = false)
    private Long storeUserId;

    @Column(name = "pay_type_code_id", nullable = false)
    private Long payTypeCodeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_type", nullable = false, length = 20)
    private PayType payType;

    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "has_national_pension")
    private Boolean hasNationalPension;

    @Column(name = "has_health_insurance")
    private Boolean hasHealthInsurance;

    @Column(name = "has_longterm_care_insurance")
    private Boolean hasLongtermCareInsurance;

    @Column(name = "has_employment_insurance")
    private Boolean hasEmploymentInsurance;

    // TODO : 필드 조정 이후 인자 추가 필요
    public static EmployeePayInfo of(
            Long storeUserId,
            Long payTypeCodeId) {
        return EmployeePayInfo.builder()
                .storeUserId(storeUserId)
                .payTypeCodeId(payTypeCodeId)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayInfo that = (EmployeePayInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

