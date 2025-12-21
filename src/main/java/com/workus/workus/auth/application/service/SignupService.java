package com.workus.workus.auth.application.service;

import com.workus.workus.auth.application.command.SignupCommand;
import com.workus.workus.auth.domain.model.UserInfo;
import com.workus.workus.auth.domain.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SignupService {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean signup(SignupCommand command) {
        if (userInfoRepository.existsByLoginId(command.loginId())) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        UserInfo userInfo = new UserInfo(
                command.loginId(),
                encodedPassword,
                command.email(),
                command.name(),
                command.phone(),
                command.agreeTerms(),
                command.agreePrivacy()
        );
        userInfoRepository.save(userInfo);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !userInfoRepository.existsByLoginId(loginId);
    }
}
