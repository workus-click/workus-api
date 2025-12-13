package com.workus.workus.schedule.domain.model;

import com.workus.workus.schedule.domain.exception.AutoSourceRequiresWorkTimeIdException;
import com.workus.workus.schedule.domain.exception.BreakTimeOutOfWorkTimeRangeException;
import com.workus.workus.schedule.domain.exception.ManualSourceMustNotHaveWorkTimeIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class WorkScheduleTest {
    private static WorkSchedule createWithWorkTimeAndBreakTime(TimeRange workTime, TimeRange breakTime) {
        return new WorkSchedule(
                1L,
                1L,
                LocalDate.of(2025,1,1),
                workTime,
                breakTime,
                WorkScheduleSource.MANUAL,
                null
        );
    }
    @Nested
    @DisplayName("스케줄생성")
    class WorkScheduleCreation {
        @Nested
        @DisplayName("근무스케줄 출처 도메인 정책")
        class WorkScheduleSourcePolicy {
            final Long workScheduleId = 1L;
            final Long storeUserId = 1L;
            final LocalDate scheduleDate = LocalDate.of(2025, 1, 1);
            final TimeRange workTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            final TimeRange breakTime = null;

            @Test
            @DisplayName("자동 스케줄은 연결 근무시간설정 없이 생성될 수 없다")
            void shouldRejectCreation_whenAutoSourceWithoutWorkTimeId() {
                assertThatThrownBy(() ->  new WorkSchedule(workScheduleId, storeUserId, scheduleDate, workTime, breakTime
                        , WorkScheduleSource.AUTO
                        , null))
                        .isInstanceOf(AutoSourceRequiresWorkTimeIdException.class);
            }
            @Test
            @DisplayName("자동 스케줄은 연결 근무시간설정이 있으면 생성된다")
            void shouldAllowCreation_whenAutoSourceWithWorkTimeId() {
                assertThatCode(() -> new WorkSchedule(workScheduleId, storeUserId, scheduleDate, workTime, breakTime
                        , WorkScheduleSource.AUTO
                        , 1L))
                        .doesNotThrowAnyException();
            }


            @Test
            @DisplayName("수동 스케줄은 연결 근무시간설정을 가질 수 없다")
            void shouldRejectCreation_whenManualSourceWithWorkTimeId() {
                assertThatThrownBy(() -> new WorkSchedule(workScheduleId, storeUserId, scheduleDate, workTime, breakTime
                        , WorkScheduleSource.MANUAL
                        , 1L))
                        .isInstanceOf(ManualSourceMustNotHaveWorkTimeIdException.class);
            }

            @Test
            @DisplayName("수동 스케줄은 연결 근무시간설정 없이 생성된다")
            void shouldAllowCreation_whenManualSourceWithoutWorkTimeId() {
                assertThatCode(() -> new WorkSchedule(workScheduleId, storeUserId, scheduleDate, workTime, breakTime
                        , WorkScheduleSource.MANUAL
                        , null))
                        .doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayName("휴게시간과 근무시간의 도메인 정책")
        class BreakTimeWithinWorkTimePolicy {
            final Long workScheduleId = 1L;
            final Long storeUserId = 1L;
            final LocalDate scheduleDate = LocalDate.of(2025, 1, 1);
            final WorkScheduleSource source = WorkScheduleSource.MANUAL;

            @Test
            @DisplayName("휴게시간이 근무시간 범위를 벗어난 경우 스케줄을 생성할 수 없다")
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
                                new WorkSchedule(
                                        workScheduleId,
                                        storeUserId,
                                        scheduleDate,
                                        workTime,
                                        breakTime,
                                        source,
                                        null
                                )
                        ).isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
                );
            }

            @Test
            @DisplayName("휴게시간이 근무시간 범위 안인 경우 스케줄을 생성할 수 있다")
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
                                new WorkSchedule(
                                        workScheduleId,
                                        storeUserId,
                                        scheduleDate,
                                        workTime,
                                        breakTime,
                                        source,
                                        null
                                )
                        ).doesNotThrowAnyException()
                );
            }

            @Test
            @DisplayName("휴게시간이 없는 경우 스케줄을 생성할 수 있다")
            void shouldAllowCreation_whenBreakTimeIsNull() {
                List<TimeRange> workTimes = List.of(
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(8, 30)),
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 30)),
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 30)),
                        new TimeRange(LocalTime.of(16, 0), LocalTime.of(19, 30)),
                        new TimeRange(LocalTime.of(18, 0), LocalTime.of(19, 30))
                );
                workTimes.forEach(workTime -> assertThatCode(() ->
                        new WorkSchedule(
                                workScheduleId,
                                storeUserId,
                                scheduleDate,
                                workTime,
                                null,
                                source,
                                null
                        )).doesNotThrowAnyException()
                );
            }
        }
    }

    @Nested
    @DisplayName("근무시간 변경 가능 여부")
    class CanChangeWorkTime {
        @Nested
        @DisplayName("기존 스케줄에 휴게시간이 없는 경우")
        class GivenNoBreakTime {
            TimeRange originalWorkTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkSchedule scheduleWithoutBreakTime = createWithWorkTimeAndBreakTime(originalWorkTime, null);

            @Test
            @DisplayName("어떤 근무시간을 전달하더라도 true를 반환한다")
            void shouldReturnTrueForAnyWorkTime() {
                List<TimeRange> newWorkTimes = List.of(
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                        new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0)),
                        new TimeRange(LocalTime.of(0, 0), LocalTime.of(23, 59))
                );

                newWorkTimes.forEach(newWorkTime ->
                        assertThat(scheduleWithoutBreakTime.canChangeWorkTime(newWorkTime))
                                .as("no breakTime → always true (newWorkTime=%s)", newWorkTime)
                                .isTrue()
                );
            }
        }

        @Nested
        @DisplayName("기존 스케줄에 휴게시간이 존재하는 경우")
        class GivenBreakTimeExists {
            final TimeRange originalWorkTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            final TimeRange breakTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
            final WorkSchedule scheduleWithBreakTime = createWithWorkTimeAndBreakTime(originalWorkTime, breakTime);

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하면 true를 반환한다")
            void shouldReturnTrue_whenNewWorkTimeContainsBreakTime() {
                var workTimesContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(18, 0)),   // 앞/뒤로 확장
                        new TimeRange(LocalTime.of(11, 0), LocalTime.of(14, 0)),  // 가운데 포함
                        new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))   // 휴게시간과 동일
                );
                workTimesContainingBreakTime.forEach(newWorkTime ->
                        assertThat(scheduleWithBreakTime.canChangeWorkTime(newWorkTime))
                                .as("breakTime %s should be within newWorkTime %s", breakTime, newWorkTime)
                                .isTrue()
                );
            }

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하지 않으면 false를 반환한다")
            void thenReturnFalse_whenNewWorkTimeDoesNotContainBreakTime() {
                var workTimesNotContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(11, 0)),   // 휴게시간 전까지만
                        new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)),  // 휴게시간 이후만
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(11, 59)),  // 끝이 휴게 시작 전
                        new TimeRange(LocalTime.of(13, 1), LocalTime.of(18, 0))   // 시작이 휴게 끝 이후
                );

                workTimesNotContainingBreakTime.forEach(newWorkTime ->
                        assertThat(scheduleWithBreakTime.canChangeWorkTime(newWorkTime))
                                .as("breakTime %s should NOT be within newWorkTime %s", breakTime, newWorkTime)
                                .isFalse()
                );
            }
        }

        @Test
        @DisplayName("새로운 근무시간이 null인 경우 예외를 던진다")
        void shouldThrowNPE_whenNewWorkTimeIsNull() {
            TimeRange originalWork = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(originalWork, null);

            assertThatThrownBy(() -> schedule.canChangeWorkTime(null))
                    .isInstanceOf(NullPointerException.class);
        }

    }

    @Nested
    @DisplayName("근무시간 변경")
    class ChangeWorkTime {
        @Nested
        @DisplayName("기존 스케줄에 휴게시간이 없는 경우")
        class GivenNoBreakTime {
            TimeRange originalWorkTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkSchedule scheduleWithoutBreakTime = createWithWorkTimeAndBreakTime(originalWorkTime, null);

            @Test
            @DisplayName("어떤 근무시간으로도 예외 없이 변경할 수 있다")
            void shouldAllowChangeToAnyWorkTime() {
                List<TimeRange> newWorkTimes = List.of(
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                        new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0)),
                        new TimeRange(LocalTime.of(0, 0), LocalTime.of(23, 59))
                );

                newWorkTimes.forEach(newWorkTime ->
                        assertThatCode(() -> scheduleWithoutBreakTime.changeWorkTime(newWorkTime))
                                .doesNotThrowAnyException()
                );
            }
        }

        @Nested
        @DisplayName("기존 스케줄에 휴게시간이 존재하는 경우")
        class GivenBreakTimeExists {
            final TimeRange originalWorkTime = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            final TimeRange breakTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
            WorkSchedule scheduleWithBreakTime = createWithWorkTimeAndBreakTime(originalWorkTime, breakTime);

            @Test
            @DisplayName("새 근무시간이 휴게시간을 포함하면 변경이 허용된다")
            void shouldAllowChange_whenNewWorkTimeContainsBreakTime() {
                var workTimesContainingBreakTime = List.of(
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(18, 0)),   // 앞/뒤로 확장
                        new TimeRange(LocalTime.of(11, 0), LocalTime.of(14, 0)),  // 가운데만 포함
                        new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))   // 휴게시간과 동일
                );

                workTimesContainingBreakTime.forEach(newWorkTime ->
                        assertThatCode(() -> scheduleWithBreakTime.changeWorkTime(newWorkTime))
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
                        assertThatThrownBy(() -> scheduleWithBreakTime.changeWorkTime(newWorkTime))
                                .isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
                );
            }
        }

        @Test
        @DisplayName("새로운 근무시간이 null인 경우 변경이 거부된다")
        void shouldRejectChange_whenNewWorkTimeIsNull() {
            TimeRange originalWork = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(originalWork, null);

            assertThatThrownBy(() -> schedule.changeWorkTime(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("휴게시간 변경 가능 여부")
    class CanChangeBreakTime {
        @Test
        @DisplayName("휴게시간을 null로 변경(제거)하는 것은 항상 허용된다")
        void shouldReturnTrue_whenNewBreakTimeIsNull() {
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(14, 0))
                    , null
            );
            assertThat(schedule.canChangeBreakTime(null)).isTrue();
        }

        @Test
        @DisplayName("근무시간 범위 안의 휴게시간은 true를 반환한다")
        void shouldReturnTrue_whenNewBreakTimeWithinWorkTime() {
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0))
                    , null
            );
            List<TimeRange> newBreakTimes = List.of(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    new TimeRange(LocalTime.of(16, 30), LocalTime.of(17, 0))
            );

            newBreakTimes.forEach(newBreakTime ->
                    assertThat(schedule.canChangeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .isTrue()
            );
        }

        @Test
        @DisplayName("근무시간 범위 밖의 휴게시간은 false를 반환한다")
        void shouldReturnFalse_whenNewBreakTimeOutsideWorkTime() {
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0))
                    , null
            );
            List<TimeRange> invalidBreakTimes = List.of(
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(8, 30)),
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(16, 0), LocalTime.of(19, 30)),
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 30))
            );

            invalidBreakTimes.forEach(newBreakTime ->
                    assertThat(schedule.canChangeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .isFalse()
            );
        }
    }

    @Nested
    @DisplayName("스케줄 내 휴게시간 변경")
    class ChangeBreakTime {
        @Test
        @DisplayName("근무시간 범위 안의 휴게시간으로 변경이 허용된다")
        void shouldAllowChange_whenNewBreakTimeWithinWorkTime() {
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0))
                    , null
            );
            List<TimeRange> newBreakTimes = List.of(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    new TimeRange(LocalTime.of(16, 30), LocalTime.of(17, 0))
            );

            newBreakTimes.forEach(newBreakTime ->
                    assertThatCode(() -> schedule.changeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .doesNotThrowAnyException()
            );
        }

        @Test
        @DisplayName("근무시간 범위 밖의 휴게시간으로 변경은 거부된다")
        void shouldRejectChange_whenNewBreakTimeOutsideWorkTime() {
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(14, 0))
                    , null
            );
            List<TimeRange> invalidBreakTimes = List.of(
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(8, 30)),
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 30)),
                    new TimeRange(LocalTime.of(16, 0), LocalTime.of(19, 30)),
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 30))
            );

            invalidBreakTimes.forEach(newBreakTime ->
                    assertThatThrownBy(() -> schedule.changeBreakTime(newBreakTime))
                            .as("newBreakTime=%s", newBreakTime)
                            .isInstanceOf(BreakTimeOutOfWorkTimeRangeException.class)
            );
        }

        @Test
        @DisplayName("휴게시간을 null로 변경(제거)하는 것은 항상 허용된다")
        void shouldAllowChange_whenNewBreakTimeIsNull() {
            WorkSchedule schedule = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(14, 0))
                    , null
            );
            assertThatCode(() -> schedule.changeBreakTime(null))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("근무시간 겹침 판단")
    class WorkTimeOverlaps {
        private WorkSchedule createWithScheduleDateAndWorkTime(LocalDate scheduleDate, TimeRange workTime) {
            return new WorkSchedule(
                    1L,
                    1L,
                    scheduleDate,
                    workTime,
                    null,
                    WorkScheduleSource.MANUAL,
                    null
            );
        }
        @Test
        @DisplayName("근무시간이 겹치는 경우 true를 반환한다")
        void shouldReturnTrue_whenWorkTimesOverlap() {
            WorkSchedule schedule = createWithScheduleDateAndWorkTime(
                    LocalDate.of(2025,1,1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            List<WorkSchedule> otherSchedules = List.of(
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(8, 0), LocalTime.of(10, 0))  // 앞부분 겹침
                    ),
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(11, 0), LocalTime.of(13, 0)) // 뒷부분 겹침
                    ),
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))  // 완전 동일
                    ),
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0))  // 완전 포함
                    ),
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2024,12,31),
                            new TimeRange(LocalTime.of(23, 30), LocalTime.of(9, 10))   // 익일 근무 앞부분 겹침
                    )
            );

            assertThat(otherSchedules).allMatch(other ->
                    schedule.workTimeOverlaps(other) && other.workTimeOverlaps(schedule)
            );
        }

        @Test
        @DisplayName("[start, end) 기준 경계만 맞닿는 경우 false를 반환한다 (겹치지 않음)")
        void shouldReturnFalse_whenWorkTimesTouchAtBoundary() {
            WorkSchedule schedule = createWithScheduleDateAndWorkTime(
                    LocalDate.of(2025,1,1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            List<WorkSchedule> otherSchedules = List.of(
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(6, 0), LocalTime.of(9, 0))  // 앞부분 경계
                    ),
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(12, 0), LocalTime.of(16, 0)) // 뒷부분 경계
                    )
            );

            assertThat(otherSchedules).allMatch(other ->
                    !schedule.workTimeOverlaps(other) && !other.workTimeOverlaps(schedule)
            );
        }

        @Test
        @DisplayName("근무시간이 완전히 분리된 경우 false를 반환한다")
        void shouldReturnTrue_whenWorkTimesDoNotOverlap() {
            WorkSchedule schedule = createWithScheduleDateAndWorkTime(
                    LocalDate.of(2025,1,1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            List<WorkSchedule> otherSchedules = List.of(
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,1),
                            new TimeRange(LocalTime.of(2, 0), LocalTime.of(8, 0))  // 시간
                    ),
                    createWithScheduleDateAndWorkTime(
                            LocalDate.of(2025,1,2),
                            new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)) // 날짜
                    )
            );

            assertThat(otherSchedules).allMatch(other ->
                    !schedule.workTimeOverlaps(other) && !other.workTimeOverlaps(schedule)
            );
        }
    }

    @Nested
    @DisplayName("야간 근무 판단")
    class IsOvernightWork {
        @Test
        @DisplayName("종료시간이 시작시간보다 이전이면(익일로 넘어가면) true를 반환한다")
        void shouldReturnTrue_whenWorkTimeSpansNextDay() {
            WorkSchedule ws = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0))
                    , null
            );
            assertThat(ws.isOvernightWork()).isTrue();
        }

        @Test
        @DisplayName("종료시간이 시작시간보다 이후면 false를 반환한다")
        void shouldReturnFalse_whenWorkTimeDoesNotSpanNextDay() {
            WorkSchedule ws = createWithWorkTimeAndBreakTime(
                    new TimeRange(LocalTime.of(1, 0), LocalTime.of(6, 0))
                    , null
            );
            assertThat(ws.isOvernightWork()).isFalse();
        }
    }
}