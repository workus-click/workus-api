package com.workus.workus.attend.config.domain.domain.model;

import com.workus.workus.attend.config.domain.model.EmployeeAttendInfo;
import com.workus.workus.attend.config.domain.model.EmployeeType;
import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import com.workus.workus.attend.common.vo.TimeRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class EmployeeAttendInfoTest {
    private static WorkTimeConfig createWorkTimeConfig() {
        return WorkTimeConfig.of(
                1L,
                EmployeeType.PART_TIME,
                "테스트 근무시간",
                new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
        );
    }

    @Nested
    @DisplayName("사원 근무 정보 생성")
    class EmployeeAttendInfoCreation {
        @Test
        @DisplayName("정상적인 파라미터로 생성할 수 있다")
        void shouldCreate_whenValidParameters() {
            Long storeUserId = 1L;
            DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;
            WorkTimeConfig workTimeConfig = createWorkTimeConfig();

            assertThatCode(() ->
                    EmployeeAttendInfo.of(storeUserId, dayOfWeek, workTimeConfig)
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("삭제")
    class Delete {
        @Test
        @DisplayName("삭제하면 isDeleted가 true가 된다")
        void shouldSetIsDeletedToTrue_whenDelete() {
            EmployeeAttendInfo info = EmployeeAttendInfo.of(
                    1L,
                    DayOfWeek.FRIDAY,
                    createWorkTimeConfig()
            );

            info.delete();

            assertThat(info.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복구")
    class Restore {
        @Test
        @DisplayName("복구하면 isDeleted가 false가 된다")
        void shouldSetIsDeletedToFalse_whenRestore() {
            EmployeeAttendInfo info = EmployeeAttendInfo.of(
                    1L,
                    DayOfWeek.FRIDAY,
                    createWorkTimeConfig()
            );
            info.delete();

            info.restore();

            assertThat(info.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("근무시간 설정 변경")
    class ChangeWorkTimeConfig {
        @Test
        @DisplayName("근무시간 설정을 변경할 수 있다")
        void shouldChangeWorkTimeConfig() {
            EmployeeAttendInfo info = EmployeeAttendInfo.of(
                    1L,
                    DayOfWeek.FRIDAY,
                    createWorkTimeConfig()
            );
            WorkTimeConfig newWorkTimeConfig = WorkTimeConfig.of(
                    1L,
                    EmployeeType.FULL_TIME,
                    "정규직 근무시간",
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );

            info.changeWorkTimeConfig(newWorkTimeConfig);

            assertThat(info.getWorkTimeConfig()).isEqualTo(newWorkTimeConfig);
        }
    }
}

