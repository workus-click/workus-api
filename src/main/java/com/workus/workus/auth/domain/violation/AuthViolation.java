package com.workus.workus.auth.domain.violation;

public sealed interface AuthViolation
	permits AuthViolation.Login, AuthViolation.Session, AuthViolation.Signup {

	sealed interface Signup extends AuthViolation
		permits LoginIdAlreadyUsed {
	}

	sealed interface Login extends AuthViolation
		permits InvalidCredentials {
	}

	sealed interface Session extends AuthViolation
		permits SessionNotIssued, SessionNotRevoked {
	}

	record LoginIdAlreadyUsed(String loginId) implements Signup {
	}

	record InvalidCredentials() implements Login {
	}

	record SessionNotIssued() implements Session {
	}

	record SessionNotRevoked() implements Session {
	}
}
