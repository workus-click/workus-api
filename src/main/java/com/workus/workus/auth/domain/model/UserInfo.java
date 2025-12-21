package com.workus.workus.auth.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserInfo extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "is_terms_agreed", nullable = false)
    private boolean termsAgreed;

    @Column(name = "is_privacy_agreed", nullable = false)
    private boolean privacyAgreed;

    public UserInfo(
            String loginId,
            String password,
            String email,
            String name,
            String phone,
            boolean termsAgreed,
            boolean privacyAgreed
    ) {
        this.userId = IdGenerator.nextId();
        this.loginId = Objects.requireNonNull(loginId);
        this.password = Objects.requireNonNull(password);
        this.email = Objects.requireNonNull(email);
        this.name = Objects.requireNonNull(name);
        this.phone = Objects.requireNonNull(phone);
        this.termsAgreed = termsAgreed;
        this.privacyAgreed = privacyAgreed;
    }
}
