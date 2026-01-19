package com.workus.workus.common.session;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorkusUser implements Serializable {
	private final Long userId;
	private final String loginId;
	private final String email;
	private final String name;
	private final String phone;

	public WorkusUser(
		Long userId,
		String loginId,
		String email,
		String name,
		String phone
	) {
		this.userId = userId;
		this.loginId = loginId;
		this.email = email;
		this.name = name;
		this.phone = phone;
	}
}
