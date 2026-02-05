package com.workus.workus.common.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class Actor implements Serializable, UserDetails, CredentialsContainer {
	private final Long userId;
	private final String loginId;
	private final String email;
	private final String name;
	private final String phone;
	private String password;
	private final List<Long> manageStores = new ArrayList<>();

	public Actor(
		Long userId,
		String loginId,
		String email,
		String name,
		String phone
	) {
		this(userId, loginId, null, email, name, phone);
	}

	public Actor(
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
		return List.of();
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
