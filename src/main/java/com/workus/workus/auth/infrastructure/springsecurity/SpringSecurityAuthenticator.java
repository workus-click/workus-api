package com.workus.workus.auth.infrastructure.springsecurity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.workus.workus.auth.application.boundary.Authenticator;
import com.workus.workus.auth.domain.violation.AuthViolation;
import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.WorkusUser;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuthenticator implements Authenticator {
	private final AuthenticationManager authenticationManager;

	@Override
	public Result<WorkusUser, AuthViolation.Login> authenticate(String username, String password) {
		try {
			SpringSecurityUser principal = (SpringSecurityUser) authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(username, password)).getPrincipal();
			WorkusUser workusUser = new WorkusUser(
				principal.getUserId(),
				principal.getLoginId(),
				principal.getEmail(),
				principal.getName(),
				principal.getPhone()
			);
			return Result.success(workusUser);
		} catch (AuthenticationException ex) {
			return Result.failure(new AuthViolation.InvalidCredentials());
		}
	}
}
