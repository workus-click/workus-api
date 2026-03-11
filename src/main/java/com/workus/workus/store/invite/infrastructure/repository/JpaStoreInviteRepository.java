package com.workus.workus.store.invite.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workus.workus.store.invite.domain.model.StoreInvite;

public interface JpaStoreInviteRepository extends JpaRepository<StoreInvite, Long> {
    Optional<StoreInvite> findByInviteToken(String inviteToken);
}
