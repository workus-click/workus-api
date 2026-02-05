package com.workus.workus.auth.infrastructure.springsecurity;

import com.workus.workus.auth.application.boundary.CredentialIssuer;
import com.workus.workus.common.session.Actor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityIssuer implements CredentialIssuer {
	private final SecurityContextRepository securityContextRepository;

	@Override
	public boolean issue(Actor workusUser) {
		try {
			ServletRequestAttributes attributes =
				(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(workusUser, null, workusUser.getAuthorities());

			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
			securityContextRepository.saveContext(context,
				attributes.getRequest(),
				attributes.getResponse());
			return true;
		} catch (RuntimeException ex) {
			return false;
		}
	}

	@Override
	public boolean revoke(Actor workusUser) {
		try {
			SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
			handler.logout(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest(),
				((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse(),
				SecurityContextHolder.getContext().getAuthentication());
			return true;
		} catch (RuntimeException ex) {
			return false;
		}
	}
}
