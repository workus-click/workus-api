package com.workus.workus.auth.infrastructure.springsecurity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.workus.workus.auth.application.boundary.CredentialIssuer;
import com.workus.workus.auth.domain.violation.AuthViolation;
import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.WorkusUser;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityIssuer implements CredentialIssuer {
	private final SecurityContextRepository securityContextRepository;

	@Override
	public Result<WorkusUser, AuthViolation.Session> issue(WorkusUser workusUser) {
		ServletRequestAttributes attributes =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		SpringSecurityUser principal = toPrincipal(workusUser);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		securityContextRepository.saveContext(context,
			attributes.getRequest(),
			attributes.getResponse());
		return Result.success(workusUser);
	}

	@Override
	public Result<Boolean, AuthViolation.Session> revoke(WorkusUser workusUser) {
		try {
			SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
			handler.logout(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest(),
				((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse(),
				SecurityContextHolder.getContext().getAuthentication());
			return Result.success(null);
		} catch (RuntimeException ex) {
			return Result.failure(null);
		}
	}

	private SpringSecurityUser toPrincipal(WorkusUser workusUser) {
		SpringSecurityUser principal = new SpringSecurityUser(
			workusUser.getUserId(),
			workusUser.getLoginId(),
			null,
			workusUser.getEmail(),
			workusUser.getName(),
			workusUser.getPhone()
		);
		return principal;
	}
}
