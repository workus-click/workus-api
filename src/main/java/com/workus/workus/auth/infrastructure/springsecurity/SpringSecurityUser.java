package com.workus.workus.auth.infrastructure.springsecurity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpringSecurityUser implements UserDetails, CredentialsContainer, Serializable {

	private final Long userId;
	private final String loginId;
	private final String email;
	private final String name;
	private final String phone;
	private String password;

	public SpringSecurityUser(
		Long userId,
		String loginId,
		String password,
		String email,
		String name,
		String phone
	) {
		this.userId = userId;
		this.loginId = loginId;
		this.password = password;
		this.email = email;
		this.name = name;
		this.phone = phone;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return loginId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void eraseCredentials() {
		this.password = null;
	}
}
