package com.workus.workus.auth.presentation.controller;

import com.workus.workus.auth.application.command.SignupCommand;
import com.workus.workus.auth.application.service.LoginService;
import com.workus.workus.auth.application.service.SignupService;
import com.workus.workus.auth.presentation.dto.LoginRequest;
import com.workus.workus.auth.presentation.dto.SignupRequest;
import com.workus.workus.common.presentation.dto.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SignupService signupService;
    private final LoginService loginService;

    @GetMapping("/check-id")
    public Response<Void> checkId(
            @RequestParam String loginId
    ) {
        boolean available = signupService.isLoginIdAvailable(loginId);
        long code = available ? 0L : -1L;
        return Response.of(code, "", null);
    }

    @PostMapping("/signup")
    public Response<Void> signup(@Valid @RequestBody SignupRequest request) {
        String email = request.resolveEmail();

        SignupCommand command = new SignupCommand(
                request.name(),
                request.loginId(),
                request.password(),
                request.phone(),
                email,
                Boolean.TRUE.equals(request.agreeTerms()),
                Boolean.TRUE.equals(request.agreePrivacy())
        );
        boolean created = signupService.signup(command);
        if (!created) {
            return Response.of(-2L, "아이디가 이미 사용 중입니다.", null);
        }
        return Response.of(0L, "성공", null);
    }

    @PostMapping("/login")
    public Response<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        boolean success = loginService.login(request.loginId(), request.password(), httpRequest, httpResponse);
        if (!success) {
            return Response.of(-1L, "아이디 또는 비밀번호가 올바르지 않습니다.", null);
        }

        return Response.of(0L, "성공", null);
    }
}
