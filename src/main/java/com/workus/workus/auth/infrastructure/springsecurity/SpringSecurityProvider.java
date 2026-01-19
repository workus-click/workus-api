package com.workus.workus.auth.infrastructure.springsecurity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.workus.workus.auth.domain.model.UserInfo;
import com.workus.workus.auth.domain.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityProvider implements UserDetailsService {
	private final UserInfoRepository userInfoRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo userInfo = userInfoRepository.findByLoginId(username)
			.orElseThrow(() -> new UsernameNotFoundException(null));
		return new SpringSecurityUser(
			userInfo.getUserId(),
			userInfo.getLoginId(),
			userInfo.getPassword(),
			userInfo.getEmail(),
			userInfo.getName(),
			userInfo.getPhone()
		);
	}
}
