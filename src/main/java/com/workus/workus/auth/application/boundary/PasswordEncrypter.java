package com.workus.workus.auth.application.boundary;

public interface PasswordEncrypter {
	String encrypt(String password);
}
