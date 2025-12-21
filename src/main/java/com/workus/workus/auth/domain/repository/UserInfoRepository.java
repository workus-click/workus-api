package com.workus.workus.auth.domain.repository;

import com.workus.workus.auth.domain.model.UserInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    boolean existsByLoginId(String loginId);
    Optional<UserInfo> findByLoginId(String loginId);
}
