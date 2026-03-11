package com.workus.workus.store.invite.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import com.github.f4b6a3.tsid.TsidFactory;
import com.workus.workus.common.component.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {IdGenerator.class, TsidFactory.class})
class StoreInviteTest {

    @Test
    void issue_호출시_pending_상태로_생성된다() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(72);

        StoreInvite invite = StoreInvite.issue(
            1L,
            "홍길동",
            "01012345678",
            "1234561234567",
            "token",
            expiresAt,
            10L
        );

        assertThat(invite.getInviteStatus()).isEqualTo(InviteStatus.PENDING);
        assertThat(invite.getAcceptedAt()).isNull();
        assertThat(invite.getAcceptedUserId()).isNull();
    }

    @Test
    void 만료처리시_expired_상태로_변경된다() {
        StoreInvite invite = StoreInvite.issue(
            1L,
            "홍길동",
            "01012345678",
            "1234561234567",
            "token",
            LocalDateTime.now().plusHours(72),
            10L
        );

        invite.markExpired(LocalDateTime.now(), 10L);

        assertThat(invite.getInviteStatus()).isEqualTo(InviteStatus.EXPIRED);
    }

    @Test
    void 승인시_accepted_상태와_승인정보가_저장된다() {
        LocalDateTime acceptedAt = LocalDateTime.now();

        StoreInvite invite = StoreInvite.issue(
            1L,
            "홍길동",
            "01012345678",
            "1234561234567",
            "token",
            acceptedAt.plusHours(10),
            10L
        );

        invite.accept(20L, acceptedAt, 20L);

        assertThat(invite.getInviteStatus()).isEqualTo(InviteStatus.ACCEPTED);
        assertThat(invite.getAcceptedUserId()).isEqualTo(20L);
        assertThat(invite.getAcceptedAt()).isEqualTo(acceptedAt);
    }

    @Test
    void pending_상태가_아니면_승인할_수_없다() {
        StoreInvite invite = StoreInvite.issue(
            1L,
            "홍길동",
            "01012345678",
            "1234561234567",
            "token",
            LocalDateTime.now().plusHours(72),
            10L
        );

        invite.markExpired(LocalDateTime.now(), 10L);

        assertThatThrownBy(() -> invite.accept(20L, LocalDateTime.now(), 20L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("PENDING 상태의 초대만 승인할 수 있습니다.");
    }
}
