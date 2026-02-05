package com.workus.workus.auth.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workus.workus.auth.application.boundary.Authenticator;
import com.workus.workus.auth.application.boundary.CredentialIssuer;
import com.workus.workus.auth.application.boundary.PasswordEncrypter;
import com.workus.workus.auth.application.command.LoginCommand;
import com.workus.workus.auth.application.command.LogoutCommand;
import com.workus.workus.auth.application.command.SignupCommand;
import com.workus.workus.auth.domain.model.UserInfo;
import com.workus.workus.auth.domain.repository.UserInfoRepository;
import com.workus.workus.auth.domain.violation.AuthViolation;
import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.Actor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final Authenticator authenticator;
	private final CredentialIssuer credentialIssuer;
	private final UserInfoRepository userInfoRepository;
	private final PasswordEncrypter passwordEncrypter;

	public Result<Boolean, AuthViolation.Login> login(LoginCommand command) {
		Optional<Actor> user = authenticator.authenticate(command.loginId(), command.password());
		if (user.isEmpty()) {
			return Result.failure(new AuthViolation.InvalidCredentials());
		}
		boolean issued = credentialIssuer.issue(user.get());
		return Result.success(issued);
	}

	public Result<Boolean, AuthViolation.Session> logout(LogoutCommand logoutCommand) {
		boolean revoked = credentialIssuer.revoke(logoutCommand.workusUser());
		if (!revoked) {
			return Result.failure(new AuthViolation.SessionNotRevoked());
		}
		return Result.success(true);
	}

	@Transactional
	public Result<Void, AuthViolation.Signup> signup(SignupCommand command) {
		if (userInfoRepository.existsByLoginId(command.loginId())) {
			return Result.failure(new AuthViolation.LoginIdAlreadyUsed(command.loginId()));
		}

		UserInfo userInfo = new UserInfo(
			command.loginId(),
			passwordEncrypter.encrypt(command.password()),
			command.email(),
			command.name(),
			command.phone(),
			command.agreeTerms(),
			command.agreePrivacy()
		);
		userInfoRepository.save(userInfo);
		return Result.success(null);
	}

	@Transactional(readOnly = true)
	public Result<Void, AuthViolation.Signup> isLoginIdAvailable(String loginId) {
		if (userInfoRepository.existsByLoginId(loginId)) {
			return Result.failure(new AuthViolation.LoginIdAlreadyUsed(loginId));
		}
		return Result.success(null);
	}
}
