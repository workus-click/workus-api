package com.workus.workus.auth.application.service;

import com.workus.workus.auth.domain.model.UserInfo;
import com.workus.workus.auth.domain.repository.UserInfoRepository;
import com.workus.workus.auth.application.session.WorkusSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {
    private final UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new WorkusSession(
                userInfo.getUserId(),
                userInfo.getLoginId(),
                userInfo.getName(),
                userInfo.getPassword()
        );
    }
}
