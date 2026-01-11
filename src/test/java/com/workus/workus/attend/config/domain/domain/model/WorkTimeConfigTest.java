package com.workus.workus.attend.config.domain.domain.model;

import com.workus.workus.attend.config.domain.exception.BreakTimeOutOfWorkTimeRangeException;
import com.workus.workus.attend.config.domain.model.EmployeeType;
import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import com.workus.workus.attend.common.vo.TimeRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class WorkTimeConfigTest {
    private static WorkTimeConfig createWithWorkTimeAndBreakTime(TimeRange workTime, TimeRange breakTime) {
        return WorkTimeConfig.of(
                1L,
                EmployeeType.PART_TIME,
                "테스트 근무시간",
                workTime,
                breakTime
        );
    }

    @Nested
    @DisplayName("근무시간 설정 생성")
    class WorkTimeConfigCreation {
        final Long storeId = 1L;
        final EmployeeType workerType = EmployeeType.PART_TIME;

        @Nested
        @DisplayName("휴게시간과 근무시간의 도메인 정책")
        class BreakTimeWithinWorkTimePolicy {
            @Test
            @DisplayName("휴게시간이 근무시간 범위를 벗어난 경우 생성할 수 없다")
            void shouldRejectCreation_whenBreakTimeOutOfWorkTimeRange() {
                TimeRange workTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));

                List<TimeRange> breakTimesOutsideWork = List.of(
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(8, 30)),
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 30)),
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 30)),
                        new TimeRange(LocalTime.of(16, 0), LocalTime.of(19, 30)),
                        new TimeRange(LocalTime.of(18, 0), LocalTime.of(19, 30))
                );
                breakTimesOutsideWork.forEach(breakTime ->
                        assertThatThrownBy(() ->
                                WorkTimeConfig.of(storeId, workerType, "테스트 근무시간", workTime, breakTime)
                        ).isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
                );
            }

            @Test
            @DisplayName("휴게시간이 근무시간 범위 안인 경우 생성할 수 있다")
            void shouldAllowCreation_whenBreakTimeWithinWorkTime() {
                TimeRange workTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
                List<TimeRange> breakTimesWithinWork = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                        new TimeRange(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                        new TimeRange(LocalTime.of(13, 0), LocalTime.of(16, 0)),
                        new TimeRange(LocalTime.of(16, 30), LocalTime.of(17, 0))
                );
                breakTimesWithinWork.forEach(breakTime ->
                        assertThatCode(() ->
                                WorkTimeConfig.of(storeId, workerType, "테스트 근무시간", workTime, breakTime)
                        ).doesNotThrowAnyException()
                );
            }

            @Test
            @DisplayName("근무시간이 다음날 넘어가는 경우 휴게시간이 범위 안에 있어야 한다")
            void shouldAllowCreation_whenWorkTimeSpansNextDayAndBreakTimeWithin() {
                TimeRange workTime = new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0));
                List<TimeRange> breakTimesWithinWork = List.of(
                        new TimeRange(LocalTime.of(23, 0), LocalTime.of(1, 0)),
                        new TimeRange(LocalTime.of(0, 0), LocalTime.of(2, 0)),
                        new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0))
                );
                breakTimesWithinWork.forEach(breakTime ->
                        assertThatCode(() ->
                                WorkTimeConfig.of(storeId, workerType, "테스트 근무시간", workTime, breakTime)
                        ).doesNotThrowAnyException()
                );
            }

            @Test
            @DisplayName("근무시간이 다음날 넘어가는 경우 휴게시간이 범위를 벗어나면 생성할 수 없다")
            void shouldRejectCreation_whenWorkTimeSpansNextDayAndBreakTimeOut() {
                TimeRange workTime = new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0));
                List<TimeRange> breakTimesOutsideWork = List.of(
                        new TimeRange(LocalTime.of(21, 0), LocalTime.of(22, 30)),
                        new TimeRange(LocalTime.of(5, 0), LocalTime.of(7, 0)),
                        new TimeRange(LocalTime.of(20, 0), LocalTime.of(23, 0))
                );
                breakTimesOutsideWork.forEach(breakTime ->
                        assertThatThrownBy(() ->
                                WorkTimeConfig.of(storeId, workerType, "테스트 근무시간", workTime, breakTime)
                        ).isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
                );
            }
        }
    }

    @Nested
    @DisplayName("근무시간 변경 가능 여부")
    class CanChangeWorkTime {
        @Nested
        @DisplayName("기존 설정에 휴게시간이 존재하는 경우")
        class GivenBreakTimeExists {
            final TimeRange originalWorkTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            final TimeRange breakTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
            final WorkTimeConfig config = createWithWorkTimeAndBreakTime(originalWorkTime, breakTime);

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하면 true를 반환한다")
            void shouldReturnTrue_whenNewWorkTimeContainsBreakTime() {
                var workTimesContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(18, 0)),   // 앞/뒤로 확장
                        new TimeRange(LocalTime.of(11, 0), LocalTime.of(14, 0)),  // 가운데 포함
                        new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))   // 휴게시간과 동일
                );
                workTimesContainingBreakTime.forEach(newWorkTime ->
                        assertThat(config.canChangeWorkTime(newWorkTime))
                                .as("breakTime %s should be within newWorkTime %s", breakTime, newWorkTime)
                                .isTrue()
                );
            }

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하지 않으면 false를 반환한다")
            void shouldReturnFalse_whenNewWorkTimeDoesNotContainBreakTime() {
                var workTimesNotContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(11, 0)),   // 휴게시간 전까지만
                        new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)),  // 휴게시간 이후만
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(11, 59)),  // 끝이 휴게 시작 전
                        new TimeRange(LocalTime.of(13, 1), LocalTime.of(18, 0))   // 시작이 휴게 끝 이후
                );

                workTimesNotContainingBreakTime.forEach(newWorkTime ->
                        assertThat(config.canChangeWorkTime(newWorkTime))
                                .as("breakTime %s should NOT be within newWorkTime %s", breakTime, newWorkTime)
                                .isFalse()
                );
            }
        }

        @Test
        @DisplayName("새로운 근무시간이 null인 경우 예외를 던진다")
        void shouldThrowNPE_whenNewWorkTimeIsNull() {
            TimeRange originalWork = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            TimeRange breakTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(originalWork, breakTime);

            assertThatThrownBy(() -> config.canChangeWorkTime(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("근무시간 변경")
    class ChangeWorkTime {
        @Nested
        @DisplayName("기존 설정에 휴게시간이 존재하는 경우")
        class GivenBreakTimeExists {
            final TimeRange originalWorkTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            final TimeRange breakTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(originalWorkTime, breakTime);

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하면 변경이 허용된다")
            void shouldAllowChange_whenNewWorkTimeContainsBreakTime() {
                var workTimesContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(18, 0)),   // 앞/뒤로 확장
                        new TimeRange(LocalTime.of(11, 0), LocalTime.of(14, 0)),  // 가운데만 포함
                        new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))   // 휴게시간과 동일
                );

                workTimesContainingBreakTime.forEach(newWorkTime ->
                        assertThatCode(() -> config.changeWorkTime(newWorkTime))
                                .doesNotThrowAnyException()
                );
            }

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하지 않으면 변경이 거부된다")
            void shouldRejectChange_whenNewWorkTimeDoesNotContainBreakTime() {
                var workTimesNotContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(11, 0)),   // 휴게시간 전까지만
                        new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)),  // 휴게시간 이후만
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(11, 59)),  // 끝이 휴게 시작 전
                        new TimeRange(LocalTime.of(13, 1), LocalTime.of(18, 0))   // 시작이 휴게 끝 이후
                );

                workTimesNotContainingBreakTime.forEach(newWorkTime ->
                        assertThatThrownBy(() -> config.changeWorkTime(newWorkTime))
                                .isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
                );
            }
        }

        @Test
        @DisplayName("새로운 근무시간이 null인 경우 변경이 거부된다")
        void shouldRejectChange_whenNewWorkTimeIsNull() {
            TimeRange originalWork = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            TimeRange breakTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(originalWork, breakTime);

            assertThatThrownBy(() -> config.changeWorkTime(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("휴게시간 변경 가능 여부")
    class CanChangeBreakTime {
        @Test
        @DisplayName("근무시간 범위 안의 휴게시간은 true를 반환한다")
        void shouldReturnTrue_whenNewBreakTimeWithinWorkTime() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );
            List<TimeRange> newBreakTimes = List.of(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    new TimeRange(LocalTime.of(16, 30), LocalTime.of(17, 0))
            );

            newBreakTimes.forEach(newBreakTime ->
                    assertThat(config.canChangeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .isTrue()
            );
        }

        @Test
        @DisplayName("근무시간 범위 밖의 휴게시간은 false를 반환한다")
        void shouldReturnFalse_whenNewBreakTimeOutsideWorkTime() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );
            List<TimeRange> invalidBreakTimes = List.of(
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(8, 30)),
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(16, 0), LocalTime.of(19, 30)),
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 30))
            );

            invalidBreakTimes.forEach(newBreakTime ->
                    assertThat(config.canChangeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .isFalse()
            );
        }

        @Test
        @DisplayName("새로운 휴게시간이 null인 경우 예외를 던진다")
        void shouldThrowNPE_whenNewBreakTimeIsNull() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );

            assertThatThrownBy(() -> config.canChangeBreakTime(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("휴게시간 변경")
    class ChangeBreakTime {
        @Test
        @DisplayName("근무시간 범위 안의 휴게시간으로 변경이 허용된다")
        void shouldAllowChange_whenNewBreakTimeWithinWorkTime() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );
            List<TimeRange> newBreakTimes = List.of(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    new TimeRange(LocalTime.of(16, 30), LocalTime.of(17, 0))
            );

            newBreakTimes.forEach(newBreakTime ->
                    assertThatCode(() -> config.changeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .doesNotThrowAnyException()
            );
        }

        @Test
        @DisplayName("근무시간 범위 밖의 휴게시간으로 변경은 거부된다")
        void shouldRejectChange_whenNewBreakTimeOutsideWorkTime() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );
            List<TimeRange> invalidBreakTimes = List.of(
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(8, 30)),
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(16, 0), LocalTime.of(19, 30)),
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 30))
            );

            invalidBreakTimes.forEach(newBreakTime ->
                    assertThatThrownBy(() -> config.changeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
            );
        }

        @Test
        @DisplayName("새로운 휴게시간이 null인 경우 변경이 거부된다")
        void shouldRejectChange_whenNewBreakTimeIsNull() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );

            assertThatThrownBy(() -> config.changeBreakTime(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("야간 근무 판단")
    class IsNextDay {
        @Test
        @DisplayName("종료시간이 시작시간보다 이전이면(익일로 넘어가면) true를 반환한다")
        void shouldReturnTrue_whenWorkTimeSpansNextDay() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0)),
                    new TimeRange(LocalTime.of(0, 0), LocalTime.of(1, 0))
            );
            assertThat(config.isNextDay()).isTrue();
        }

        @Test
        @DisplayName("종료시간이 시작시간보다 이후면 false를 반환한다")
        void shouldReturnFalse_whenWorkTimeDoesNotSpanNextDay() {
            WorkTimeConfig config = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );
            assertThat(config.isNextDay()).isFalse();
        }
    }
}

