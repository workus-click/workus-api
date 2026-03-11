package com.workus.workus.store.application.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workus.workus.common.result.Result;
import com.workus.workus.common.session.Actor;
import com.workus.workus.store.application.command.RegisterStoreCommand;
import com.workus.workus.store.application.violation.StoreRegistrationViolation;
import com.workus.workus.store.domain.model.StoreInfo;
import com.workus.workus.store.domain.model.StoreUser;
import com.workus.workus.store.domain.repository.StoreInfoRepository;
import com.workus.workus.store.domain.repository.StoreUserRepository;

@Service
@Transactional
public class StoreRegistrationService {
    private final StoreInfoRepository storeInfoRepository;
    private final StoreUserRepository storeUserRepository;

    public StoreRegistrationService(StoreInfoRepository storeInfoRepository, StoreUserRepository storeUserRepository) {
        this.storeInfoRepository = storeInfoRepository;
        this.storeUserRepository = storeUserRepository;
    }

    public record StoreRegistrationResult(
        Long storeId,
        Long storeUserId
    ) {
    }

    public Result<StoreRegistrationResult, StoreRegistrationViolation> register(
        RegisterStoreCommand command,
        Actor actor
    ) {
        if (actor == null || actor.getUserId() == null) {
            return Result.failure(new StoreRegistrationViolation.TechnicalFailure("사용자 인증이 필요합니다."));
        }

        try {
            StoreInfo storeInfo = StoreInfo.of(
                command.storeName(),
                command.businessNumber(),
                command.representativeName(),
                command.businessType(),
                command.contactPhoneNumber(),
                command.storeZipCode(),
                command.storeAddress()
            );
            storeInfoRepository.save(storeInfo);

            StoreUser storeOwner = StoreUser.of(
                actor.getUserId(),
                storeInfo.getStoreId(),
                command.representativeName(),
                command.contactPhoneNumber(),
                null,
                actor.getUserId()
            );
            storeUserRepository.save(storeOwner);

            return Result.success(new StoreRegistrationResult(storeInfo.getStoreId(), storeOwner.getStoreUserId()));
        } catch (Exception e) {
            return Result.failure(new StoreRegistrationViolation.TechnicalFailure(
                Objects.toString(e.getMessage(), "매장 등록을 완료하지 못했습니다.")
            ));
        }
    }
}
