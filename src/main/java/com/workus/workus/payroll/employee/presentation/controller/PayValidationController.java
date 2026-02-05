package com.workus.workus.payroll.employee.presentation.controller;

import com.workus.workus.common.presentation.dto.Response;
import com.workus.workus.payroll.employee.domain.service.PayValidationService;
import com.workus.workus.payroll.employee.presentation.dto.PayValidationRequest;
import com.workus.workus.payroll.employee.presentation.dto.PayValidationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 급여 유효성 검사 API
 */
@RestController
@RequestMapping("/api/payroll/pay")
@RequiredArgsConstructor
public class PayValidationController {

    private final PayValidationService payValidationService;

    /**
     * 급여 유효성 검사
     * - 시급제: 시급 입력 시 최저시급 체크 + 월급 환산
     * - 월급제: 월급 입력 시 환산 시급 계산 + 최저시급 체크
     */
    @PostMapping("/validate")
    public Response<PayValidationResponse> validatePay(
            @Valid @RequestBody PayValidationRequest request) {
        PayValidationResponse response = payValidationService.validate(request);
        return Response.of(200, "OK", response);
    }
}
