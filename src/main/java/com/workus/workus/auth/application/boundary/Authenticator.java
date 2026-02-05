package com.workus.workus.auth.application.boundary;

import java.util.Optional;

import com.workus.workus.common.session.Actor;

public interface Authenticator {
	Optional<Actor> authenticate(String username, String password);
}
