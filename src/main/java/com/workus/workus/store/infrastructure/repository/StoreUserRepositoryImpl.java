package com.workus.workus.store.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.workus.workus.store.domain.model.StoreUser;
import com.workus.workus.store.domain.repository.StoreUserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreUserRepositoryImpl implements StoreUserRepository {
    private final JpaStoreUserRepository jpaStoreUserRepository;

    @Override
    public void save(StoreUser storeUser) {
        jpaStoreUserRepository.save(storeUser);
    }
}
