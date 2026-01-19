package com.workus.workus.auth.application.command;

import com.workus.workus.common.session.WorkusUser;

public record LogoutCommand(
	WorkusUser workusUser
) {
}
