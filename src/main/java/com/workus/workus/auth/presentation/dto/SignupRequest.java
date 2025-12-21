package com.workus.workus.auth.presentation.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String name,
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String phone,
        @NotBlank String emailId,
        @NotBlank String emailDomain,
        @AssertTrue Boolean agreeTerms,
        @AssertTrue Boolean agreePrivacy
) {
    public String resolveEmail() {
        if (emailId != null && !emailId.isBlank() && emailDomain != null && !emailDomain.isBlank()) {
            return emailId.trim() + "@" + emailDomain.trim();
        }
        return null;
    }
}
