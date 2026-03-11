package com.workus.workus.store.invite.application.command;

public record CreateStoreInviteCommand(
    String employeeName,
    String employeePhone,
    String residentNumber
) {
}
