package com.workus.workus.store.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workus.workus.store.domain.model.StoreUser;

public interface JpaStoreUserRepository extends JpaRepository<StoreUser, Long> {
}
