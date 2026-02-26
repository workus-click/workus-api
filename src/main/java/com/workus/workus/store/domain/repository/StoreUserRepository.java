package com.workus.workus.store.domain.repository;

import com.workus.workus.store.domain.model.StoreUser;

public interface StoreUserRepository {
    void save(StoreUser storeUser);
}
