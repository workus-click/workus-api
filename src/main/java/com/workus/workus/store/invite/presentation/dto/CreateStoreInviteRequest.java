package com.workus.workus.store.invite.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateStoreInviteRequest(
    @NotBlank
    @Size(max = 100)
    String employeeName,

    @NotBlank
    @Pattern(regexp = "^[0-9-]+$", message = "연락처 형식이 올바르지 않습니다.")
    @Size(max = 20)
    String employeePhone,

    @NotBlank
    @Pattern(regexp = "^\\d{13}$", message = "주민등록번호는 숫자 13자리여야 합니다.")
    String residentNumber
) {
}
