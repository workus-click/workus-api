package com.workus.workus.auth.application.command;

public record LoginCommand(
        String loginId,
        String password
) {
}
