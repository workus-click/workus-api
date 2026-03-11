package com.workus.workus.store.domain.repository;

import java.util.Optional;

import com.workus.workus.store.domain.model.StoreUser;

public interface StoreUserRepository {
    void save(StoreUser storeUser);

    boolean existsByStoreIdAndUserId(Long storeId, Long userId);

    Optional<Long> findFirstStoreIdByUserId(Long userId);
}
