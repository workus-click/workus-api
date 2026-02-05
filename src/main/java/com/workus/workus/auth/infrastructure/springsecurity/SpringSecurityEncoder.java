package com.workus.workus.auth.infrastructure.springsecurity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.workus.workus.auth.application.boundary.PasswordEncrypter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityEncoder implements PasswordEncrypter {
	private final PasswordEncoder passwordEncoder;

	@Override
	public String encrypt(String password) {
		return passwordEncoder.encode(password);
	}
}
