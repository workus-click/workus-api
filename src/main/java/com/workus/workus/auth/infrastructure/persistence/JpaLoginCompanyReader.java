package com.workus.workus.auth.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.workus.workus.auth.application.boundary.LoginCompanyReader;
import com.workus.workus.auth.application.model.LoginCompany;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaLoginCompanyReader implements LoginCompanyReader {
	private final EntityManager entityManager;

	@Override
	public List<LoginCompany> findByUserId(Long userId) {
		@SuppressWarnings("unchecked")
		List<Object[]> rows = entityManager.createNativeQuery("""
			SELECT si.store_id, si.store_name
			FROM store_user su
			JOIN store_info si ON si.store_id = su.store_id
			WHERE su.user_id = :userId
			ORDER BY si.store_name
			""")
			.setParameter("userId", userId)
			.getResultList();

		return rows.stream()
			.map(row -> new LoginCompany(((Number)row[0]).longValue(), (String)row[1]))
			.toList();
	}
}
