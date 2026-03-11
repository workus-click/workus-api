package com.workus.workus.store.invite.application.service;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.HexFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.Actor;
import com.workus.workus.store.invite.application.command.CreateStoreInviteCommand;
import com.workus.workus.store.invite.application.model.CreateStoreInviteResult;
import com.workus.workus.store.invite.application.violation.StoreInviteViolation;
import com.workus.workus.store.invite.domain.model.StoreInvite;
import com.workus.workus.store.domain.repository.StoreUserRepository;
import com.workus.workus.store.invite.domain.repository.StoreInviteRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateStoreInviteService {
    private static final long EXPIRES_AFTER_HOURS = 72L;
    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HexFormat HEX_FORMAT = HexFormat.of();

    private final StoreInviteRepository storeInviteRepository;
    private final StoreUserRepository storeUserRepository;

    public Result<CreateStoreInviteResult, StoreInviteViolation> create(
        CreateStoreInviteCommand command,
        Actor actor
    ) {
        if (actor == null || actor.getUserId() == null) {
            return Result.failure(new StoreInviteViolation.TechnicalFailure("사용자 인증이 필요합니다."));
        }

        Long storeId = storeUserRepository.findFirstStoreIdByUserId(actor.getUserId())
            .orElse(null);
        if (storeId == null) {
            return Result.failure(new StoreInviteViolation.TechnicalFailure("사용자 소속 매장을 찾을 수 없습니다."));
        }

        try {
            String token = createToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(EXPIRES_AFTER_HOURS);
            StoreInvite storeInvite = StoreInvite.issue(
                storeId,
                command.employeeName().trim(),
                command.employeePhone().trim(),
                command.residentNumber().trim(),
                token,
                expiresAt,
                actor.getUserId()
            );
            storeInviteRepository.save(storeInvite);

            return Result.success(new CreateStoreInviteResult(
                token,
                "/signup?invite=" + token,
                expiresAt
            ));
        } catch (Exception ex) {
            return Result.failure(new StoreInviteViolation.TechnicalFailure("초대 링크 생성에 실패했습니다."));
        }
    }

    private String createToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return HEX_FORMAT.formatHex(tokenBytes);
    }
}
