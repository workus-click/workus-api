package com.workus.workus.store.presentation.controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workus.workus.common.presentation.dto.APIResponse;
import com.workus.workus.common.session.Actor;
import com.workus.workus.store.application.command.RegisterStoreCommand;
import com.workus.workus.store.application.service.StoreRegistrationService;
import com.workus.workus.store.application.violation.StoreRegistrationViolation;
import com.workus.workus.store.presentation.dto.RegisterStoreRequest;
import com.workus.workus.store.presentation.dto.StoreRegistrationResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreRegistrationService storeRegistrationService;

    public StoreController(StoreRegistrationService storeRegistrationService) {
        this.storeRegistrationService = storeRegistrationService;
    }

    @PostMapping
    public ResponseEntity<APIResponse<StoreRegistrationResponse, String>> registerStore(
        @AuthenticationPrincipal Actor actor,
        @Valid @RequestBody RegisterStoreRequest request
    ) {
        RegisterStoreCommand command = new RegisterStoreCommand(
            request.storeName(),
            request.businessNumber(),
            request.representativeName(),
            request.businessType(),
            request.contactPhoneNumber(),
            request.storeZipCode(),
            request.storeAddress()
        );

        return storeRegistrationService.register(command, actor)
            .fold(
                result -> ResponseEntity.ok(APIResponse.ok(
                    "0",
                    "매장 등록이 완료되었습니다.",
                    new StoreRegistrationResponse(result.storeId(), result.storeUserId())
                )),
                failure -> switch (failure) {
                    case StoreRegistrationViolation.TechnicalFailure technicalFailure -> ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body(APIResponse.error(
                            "-1",
                            technicalFailure.reason(),
                            "TECHNICAL_FAILURE"
                        ));
                }
            );
    }
}
