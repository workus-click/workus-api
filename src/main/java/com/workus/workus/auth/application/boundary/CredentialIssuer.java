package com.workus.workus.auth.application.boundary;

import com.workus.workus.common.session.Actor;

public interface CredentialIssuer {
	boolean issue(Actor workusUser);
	boolean revoke();
}
