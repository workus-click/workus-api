package com.workus.workus.store.invite.domain.repository;

import java.util.Optional;

import com.workus.workus.store.invite.domain.model.StoreInvite;

public interface StoreInviteRepository {
    void save(StoreInvite storeInvite);

    Optional<StoreInvite> findByInviteToken(String inviteToken);
}
