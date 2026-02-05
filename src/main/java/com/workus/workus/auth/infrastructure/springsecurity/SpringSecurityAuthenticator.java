package com.workus.workus.auth.infrastructure.springsecurity;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.workus.workus.auth.application.boundary.Authenticator;
import com.workus.workus.common.session.Actor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuthenticator implements Authenticator {
	private final AuthenticationManager authenticationManager;

	@Override
	public Optional<Actor> authenticate(String username, String password) {
		try {
			Actor principal = (Actor) authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(username, password)
			).getPrincipal();
			return Optional.of(principal);
		} catch (AuthenticationException ex) {
			return Optional.empty();
		}
	}
}
