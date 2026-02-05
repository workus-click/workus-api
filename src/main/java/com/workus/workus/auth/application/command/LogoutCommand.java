package com.workus.workus.auth.application.command;

import com.workus.workus.common.session.Actor;

public record LogoutCommand(
	Actor workusUser
) {
}
