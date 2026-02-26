package com.workus.workus.store.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.workus.workus.common.component.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "store_user")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreUser {
    @Id
    @Column(name = "store_user_id")
    private Long storeUserId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(name = "emp_code", nullable = false)
    private String employeeCode;

    @Column(name = "emp_name", nullable = false)
    private String employeeName;

    @Column(name = "resident_no", nullable = false)
    private String residentNumber;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "position_code_id", nullable = false)
    private Long positionCodeId;

    @Column(name = "job_type_code_id", nullable = false)
    private Long jobTypeCodeId;

    @Column(name = "work_type_code_id", nullable = false)
    private Long workTypeCodeId;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modify_at")
    private LocalDateTime modifiedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "modify_by")
    private Long modifiedBy;

    public static StoreUser of(
        Long userId,
        Long storeId,
        String ownerName,
        String ownerPhone,
        String residentNumber,
        Long createdBy
    ) {
        return StoreUser.builder()
            .storeUserId(IdGenerator.nextId())
            .storeId(storeId)
            .userId(userId)
            .userType("A")
            .employeeCode("OWNER")
            .employeeName(ownerName)
            .residentNumber(residentNumber)
            .phone(ownerPhone)
            .positionCodeId(0L)
            .jobTypeCodeId(0L)
            .workTypeCodeId(0L)
            .hireDate(LocalDate.now())
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .createdBy(createdBy)
            .modifiedBy(createdBy)
            .build();
    }
}
