package com.workus.workus.store.invite.presentation.controller;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workus.workus.common.presentation.dto.APIResponse;
import com.workus.workus.common.session.Actor;
import com.workus.workus.store.invite.application.command.CreateStoreInviteCommand;
import com.workus.workus.store.invite.application.service.AcceptStoreInviteService;
import com.workus.workus.store.invite.application.service.CreateStoreInviteService;
import com.workus.workus.store.invite.application.service.ResolveStoreInviteService;
import com.workus.workus.store.invite.application.violation.StoreInviteViolation;
import com.workus.workus.store.invite.presentation.dto.AcceptStoreInviteResponse;
import com.workus.workus.store.invite.presentation.dto.CreateStoreInviteRequest;
import com.workus.workus.store.invite.presentation.dto.CreateStoreInviteResponse;
import com.workus.workus.store.invite.presentation.dto.ResolveStoreInviteResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store-invites")
@RequiredArgsConstructor
@Validated
public class StoreInviteController {
    private final CreateStoreInviteService createStoreInviteService;
    private final ResolveStoreInviteService resolveStoreInviteService;
    private final AcceptStoreInviteService acceptStoreInviteService;

    @PostMapping
    public ResponseEntity<APIResponse<CreateStoreInviteResponse, String>> createStoreInvite(
        @AuthenticationPrincipal Actor actor,
        @Valid @RequestBody CreateStoreInviteRequest request
    ) {
        CreateStoreInviteCommand command = new CreateStoreInviteCommand(
            request.employeeName(),
            request.employeePhone(),
            request.residentNumber()
        );

        return createStoreInviteService.create(command, actor)
            .fold(
                result -> ResponseEntity.ok(APIResponse.ok(
                    "0",
                    "초대 링크가 생성되었습니다.",
                    new CreateStoreInviteResponse(result.inviteToken(), result.inviteUrl(), result.expiresAt())
                )),
                failure -> cast(mapViolation(failure))
            );
    }

    @GetMapping("/resolve")
    public ResponseEntity<APIResponse<ResolveStoreInviteResponse, String>> resolveStoreInvite(
        @RequestParam @NotBlank String token
    ) {
        return resolveStoreInviteService.resolve(token)
            .fold(
                result -> ResponseEntity.ok(APIResponse.ok(
                    "0",
                    "초대 정보를 조회했습니다.",
                    new ResolveStoreInviteResponse(
                        result.storeName(),
                        result.employeeName(),
                        result.status(),
                        result.expiresAt()
                    )
                )),
                failure -> cast(mapViolation(failure))
            );
    }

    @PostMapping("/{token}/accept")
    public ResponseEntity<APIResponse<AcceptStoreInviteResponse, String>> acceptStoreInvite(
        @AuthenticationPrincipal Actor actor,
        @PathVariable @NotBlank String token
    ) {
        return acceptStoreInviteService.accept(token, actor)
            .fold(
                result -> ResponseEntity.ok(APIResponse.ok(
                    "0",
                    "초대 승인이 완료되었습니다.",
                    new AcceptStoreInviteResponse(result.storeId(), result.storeName(), result.acceptedAt())
                )),
                failure -> cast(mapViolation(failure))
            );
    }

    private ResponseEntity<APIResponse<?, String>> mapViolation(StoreInviteViolation failure) {
        return switch (failure) {
            case StoreInviteViolation.InvalidToken invalidToken -> ResponseEntity.status(NOT_FOUND)
                .body(APIResponse.error("-1", "유효하지 않은 초대 토큰입니다.", "INVALID_TOKEN"));
            case StoreInviteViolation.ExpiredInvite expiredInvite -> ResponseEntity.status(GONE)
                .body(APIResponse.error("-1", "만료된 초대 링크입니다.", "EXPIRED_INVITE"));
            case StoreInviteViolation.AlreadyAccepted alreadyAccepted -> ResponseEntity.status(CONFLICT)
                .body(APIResponse.error("-1", "이미 승인된 초대 링크입니다.", "ALREADY_ACCEPTED"));
            case StoreInviteViolation.IdentityMismatch identityMismatch -> ResponseEntity.status(FORBIDDEN)
                .body(APIResponse.error("-1", "초대 대상 정보와 로그인 사용자 정보가 일치하지 않습니다.", "IDENTITY_MISMATCH"));
            case StoreInviteViolation.AlreadyMember alreadyMember -> ResponseEntity.status(CONFLICT)
                .body(APIResponse.error("-1", "이미 매장에 소속된 사용자입니다.", "ALREADY_MEMBER"));
            case StoreInviteViolation.TechnicalFailure technicalFailure -> ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(APIResponse.error("-1", technicalFailure.reason(), "TECHNICAL_FAILURE"));
        };
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<APIResponse<T, String>> cast(ResponseEntity<APIResponse<?, String>> response) {
        return (ResponseEntity<APIResponse<T, String>>) (ResponseEntity<?>) response;
    }
}
