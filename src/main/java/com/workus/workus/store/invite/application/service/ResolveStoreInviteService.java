package com.workus.workus.store.invite.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workus.workus.common.result.Result;
import com.workus.workus.store.domain.model.StoreInfo;
import com.workus.workus.store.domain.repository.StoreInfoRepository;
import com.workus.workus.store.invite.application.model.ResolveStoreInviteResult;
import com.workus.workus.store.invite.application.violation.StoreInviteViolation;
import com.workus.workus.store.invite.domain.model.StoreInvite;
import com.workus.workus.store.invite.domain.repository.StoreInviteRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ResolveStoreInviteService {
    private final StoreInviteRepository storeInviteRepository;
    private final StoreInfoRepository storeInfoRepository;

    public Result<ResolveStoreInviteResult, StoreInviteViolation> resolve(String token) {
        StoreInvite storeInvite = storeInviteRepository.findByInviteToken(token)
            .orElse(null);

        if (storeInvite == null) {
            return Result.failure(new StoreInviteViolation.InvalidToken(token));
        }

        LocalDateTime now = LocalDateTime.now();
        if (storeInvite.isPending() && storeInvite.isExpired(now)) {
            storeInvite.markExpired(now, null);
            storeInviteRepository.save(storeInvite);
        }

        if (storeInvite.isExpiredStatus()) {
            return Result.failure(new StoreInviteViolation.ExpiredInvite());
        }

        if (storeInvite.isAccepted()) {
            return Result.failure(new StoreInviteViolation.AlreadyAccepted());
        }

        StoreInfo storeInfo = storeInfoRepository.findByStoreId(storeInvite.getStoreId())
            .orElse(null);

        if (storeInfo == null) {
            return Result.failure(new StoreInviteViolation.TechnicalFailure("매장 정보를 찾을 수 없습니다."));
        }

        return Result.success(new ResolveStoreInviteResult(
            storeInfo.getStoreName(),
            storeInvite.getEmployeeName(),
            storeInvite.getInviteStatus(),
            storeInvite.getExpiresAt()
        ));
    }
}
