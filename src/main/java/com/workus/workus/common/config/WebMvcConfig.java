package com.workus.workus.common.config;

import com.workus.workus.auth.infrastructure.springsecurity.SpringSecurityUser;
import com.workus.workus.common.session.WorkusUser;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

		resolvers.add(new HandlerMethodArgumentResolver() {
			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return WorkusUser.class.isAssignableFrom(parameter.getParameterType());
			}

			@Override
			public Object resolveArgument(
				MethodParameter parameter,
				ModelAndViewContainer mavContainer,
				NativeWebRequest webRequest,
				WebDataBinderFactory binderFactory
			) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication == null
					|| !authentication.isAuthenticated()
					|| authentication instanceof AnonymousAuthenticationToken) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
				}

				Object principal = authentication.getPrincipal();
				if (principal instanceof WorkusUser session) {
					return session;
				}
				if (principal instanceof SpringSecurityUser session) {
					return new WorkusUser(
						session.getUserId(),
						session.getLoginId(),
						session.getEmail(),
						session.getName(),
						session.getPhone()
					);
				}

				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
			}
		});
	}
}
