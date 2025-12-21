package com.workus.workus.auth.application.command;

public record SignupCommand(
        String name,
        String loginId,
        String password,
        String phone,
        String email,
        boolean agreeTerms,
        boolean agreePrivacy
) {}
