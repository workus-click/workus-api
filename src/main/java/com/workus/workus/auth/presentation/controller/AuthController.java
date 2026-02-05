package com.workus.workus.auth.presentation.controller;

import static org.springframework.http.ResponseEntity.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workus.workus.auth.application.command.LoginCommand;
import com.workus.workus.auth.application.command.LogoutCommand;
import com.workus.workus.auth.application.command.SignupCommand;
import com.workus.workus.auth.application.service.AuthService;
import com.workus.workus.auth.presentation.dto.LoginRequest;
import com.workus.workus.auth.presentation.dto.SignupRequest;
import com.workus.workus.common.presentation.dto.APIResponse;
import com.workus.workus.common.session.Actor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<APIResponse<Boolean,Boolean>> login(@Valid @RequestBody LoginRequest request) {
		LoginCommand command = new LoginCommand(request.loginId(), request.password());
		return authService.login(command).fold(
			success -> ok(APIResponse.ok("0", "", true)),
			violation -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(APIResponse.error(
					"-1",
					"",
					false
				))
		);
	}

	@PostMapping("/logout")
	public ResponseEntity<APIResponse<Boolean, Boolean>> logout(@AuthenticationPrincipal Actor workusUser) {
		return authService.logout(new LogoutCommand(workusUser)).fold(
			success -> ok(APIResponse.ok("0", "", true)),
			failure -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(APIResponse.error("-1", "", false))
		);
	}

	@GetMapping("/check-id")
	public ResponseEntity<APIResponse<Boolean, Boolean>> checkId(@RequestParam String loginId) {
		return authService.isLoginIdAvailable(loginId).fold(
			success -> ok(APIResponse.ok("0", "", true)),
			violation -> ResponseEntity.status(HttpStatus.CONFLICT)
				.body(APIResponse.error("-1", "아이디가 이미 사용 중입니다.", false))
		);
	}

	@PostMapping("/signup")
	public ResponseEntity<APIResponse<Boolean, Boolean>> signup(@Valid @RequestBody SignupRequest request) {
		SignupCommand command = new SignupCommand(
			request.name(),
			request.loginId(),
			request.password(),
			request.phone(),
			request.resolveEmail(),
			request.agreeTerms(),
			request.agreePrivacy()
		);
		return authService.signup(command).fold(
			success -> ok(APIResponse.ok("0", "", true)),
			violation -> ResponseEntity.badRequest()
				.body(APIResponse.error("-1", "아이디가 이미 사용 중입니다.", false))
		);
	}
}
