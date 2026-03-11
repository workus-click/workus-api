package com.workus.workus.store.invite.domain.model;

import java.time.LocalDateTime;

import com.workus.workus.common.component.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "store_invite")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreInvite {
    @Id
    @Column(name = "store_invite_id")
    private Long storeInviteId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "employee_phone", nullable = false)
    private String employeePhone;

    @Column(name = "resident_no", nullable = false)
    private String residentNumber;

    @Column(name = "invite_token", nullable = false, unique = true)
    private String inviteToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_status", nullable = false)
    private InviteStatus inviteStatus;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "accepted_user_id")
    private Long acceptedUserId;

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

    public static StoreInvite issue(
        Long storeId,
        String employeeName,
        String employeePhone,
        String residentNumber,
        String inviteToken,
        LocalDateTime expiresAt,
        Long createdBy
    ) {
        LocalDateTime now = LocalDateTime.now();
        return StoreInvite.builder()
            .storeInviteId(IdGenerator.nextId())
            .storeId(storeId)
            .employeeName(employeeName)
            .employeePhone(employeePhone)
            .residentNumber(residentNumber)
            .inviteToken(inviteToken)
            .inviteStatus(InviteStatus.PENDING)
            .expiresAt(expiresAt)
            .acceptedAt(null)
            .acceptedUserId(null)
            .createdAt(now)
            .modifiedAt(now)
            .createdBy(createdBy)
            .modifiedBy(createdBy)
            .build();
    }

    public boolean isPending() {
        return inviteStatus == InviteStatus.PENDING;
    }

    public boolean isAccepted() {
        return inviteStatus == InviteStatus.ACCEPTED;
    }

    public boolean isExpiredStatus() {
        return inviteStatus == InviteStatus.EXPIRED;
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public void markExpired(LocalDateTime now, Long modifierId) {
        if (inviteStatus == InviteStatus.ACCEPTED || inviteStatus == InviteStatus.EXPIRED) {
            return;
        }
        inviteStatus = InviteStatus.EXPIRED;
        modifiedAt = now;
        modifiedBy = modifierId;
    }

    public void accept(Long acceptedUserId, LocalDateTime acceptedAt, Long modifierId) {
        if (inviteStatus != InviteStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 초대만 승인할 수 있습니다.");
        }
        inviteStatus = InviteStatus.ACCEPTED;
        this.acceptedUserId = acceptedUserId;
        this.acceptedAt = acceptedAt;
        modifiedAt = acceptedAt;
        modifiedBy = modifierId;
    }
}
