package com.workus.workus.store.invite.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.workus.workus.store.invite.domain.model.StoreInvite;
import com.workus.workus.store.invite.domain.repository.StoreInviteRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreInviteRepositoryImpl implements StoreInviteRepository {
    private final JpaStoreInviteRepository jpaStoreInviteRepository;

    @Override
    public void save(StoreInvite storeInvite) {
        jpaStoreInviteRepository.save(storeInvite);
    }

    @Override
    public Optional<StoreInvite> findByInviteToken(String inviteToken) {
        return jpaStoreInviteRepository.findByInviteToken(inviteToken);
    }
}
