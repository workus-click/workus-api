package com.workus.workus.auth.application.boundary;

import java.util.List;

import com.workus.workus.auth.application.model.LoginCompany;

public interface LoginCompanyReader {
	List<LoginCompany> findByUserId(Long userId);
}
