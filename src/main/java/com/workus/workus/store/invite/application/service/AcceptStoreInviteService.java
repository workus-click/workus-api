package com.workus.workus.store.invite.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.Actor;
import com.workus.workus.store.domain.model.StoreInfo;
import com.workus.workus.store.domain.model.StoreUser;
import com.workus.workus.store.domain.repository.StoreInfoRepository;
import com.workus.workus.store.domain.repository.StoreUserRepository;
import com.workus.workus.store.invite.application.model.AcceptStoreInviteResult;
import com.workus.workus.store.invite.application.violation.StoreInviteViolation;
import com.workus.workus.store.invite.domain.model.StoreInvite;
import com.workus.workus.store.invite.domain.repository.StoreInviteRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptStoreInviteService {
    private final StoreInviteRepository storeInviteRepository;
    private final StoreInfoRepository storeInfoRepository;
    private final StoreUserRepository storeUserRepository;

    public Result<AcceptStoreInviteResult, StoreInviteViolation> accept(String token, Actor actor) {
        if (actor == null || actor.getUserId() == null) {
            return Result.failure(new StoreInviteViolation.TechnicalFailure("사용자 인증이 필요합니다."));
        }

        StoreInvite storeInvite = storeInviteRepository.findByInviteToken(token)
            .orElse(null);

        if (storeInvite == null) {
            return Result.failure(new StoreInviteViolation.InvalidToken(token));
        }

        LocalDateTime now = LocalDateTime.now();
        if (storeInvite.isPending() && storeInvite.isExpired(now)) {
            storeInvite.markExpired(now, actor.getUserId());
            storeInviteRepository.save(storeInvite);
        }

        if (storeInvite.isExpiredStatus()) {
            return Result.failure(new StoreInviteViolation.ExpiredInvite());
        }

        if (storeInvite.isAccepted()) {
            return Result.failure(new StoreInviteViolation.AlreadyAccepted());
        }

        if (!isIdentityMatched(storeInvite, actor)) {
            return Result.failure(new StoreInviteViolation.IdentityMismatch());
        }

        if (storeUserRepository.existsByStoreIdAndUserId(storeInvite.getStoreId(), actor.getUserId())) {
            return Result.failure(new StoreInviteViolation.AlreadyMember());
        }

        StoreInfo storeInfo = storeInfoRepository.findByStoreId(storeInvite.getStoreId())
            .orElse(null);

        if (storeInfo == null) {
            return Result.failure(new StoreInviteViolation.TechnicalFailure("매장 정보를 찾을 수 없습니다."));
        }

        StoreUser storeUser = StoreUser.ofEmployee(
            actor.getUserId(),
            storeInvite.getStoreId(),
            storeInvite.getEmployeeName(),
            storeInvite.getEmployeePhone(),
            storeInvite.getResidentNumber(),
            actor.getUserId()
        );
        storeUserRepository.save(storeUser);

        storeInvite.accept(actor.getUserId(), now, actor.getUserId());
        storeInviteRepository.save(storeInvite);

        return Result.success(new AcceptStoreInviteResult(
            storeInfo.getStoreId(),
            storeInfo.getStoreName(),
            now
        ));
    }

    private boolean isIdentityMatched(StoreInvite storeInvite, Actor actor) {
        return normalizeName(storeInvite.getEmployeeName()).equals(normalizeName(actor.getName()))
            && normalizePhone(storeInvite.getEmployeePhone()).equals(normalizePhone(actor.getPhone()));
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}
