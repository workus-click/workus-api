package com.workus.workus.auth.application.boundary;

import com.workus.workus.auth.domain.violation.AuthViolation;
import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.WorkusUser;

public interface Authenticator {
	Result<WorkusUser, AuthViolation.Login> authenticate(String username, String password);
}
