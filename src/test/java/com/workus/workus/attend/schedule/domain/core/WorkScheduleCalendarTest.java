package com.workus.workus.attend.schedule.domain.core;
import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.common.vo.TimeRange;
import com.workus.workus.attend.schedule.application.command.AddWorkScheduleCommand;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.CreateAndChange;
import com.workus.workus.attend.schedule.domain.violation.WorkScheduleRuleViolation.ScheduleConflict;
import com.workus.workus.common.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static com.workus.workus.attend.schedule.domain.core.WorkScheduleSource.ofManual;
import static java.time.Month.JANUARY;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.*;

class WorkScheduleCalendarTest {
    final Long calendarUser = 1L;

    private List<DateRange> calendarRangesOfYearMonth(int year, Month month){
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth().plusDays(1);
        return List.of(new DateRange(start, end));
    }
    private WorkAndBreakTime workAndBreakTimeWithoutBreak(TimeRange workTime) {
        return WorkAndBreakTime.create(workTime, null).getOrThrow();
    }
    private WorkSchedule createWorkSchedule(long id, LocalDate date, TimeRange workTime) {
        return new WorkSchedule(id, calendarUser, date, workAndBreakTimeWithoutBreak(workTime), ofManual());
    }
    @Nested
    @DisplayName("스케줄 추가 (addSchedule)")
    class AddSchedule {
        private AddWorkScheduleCommand commandWithUserAndScheduleDate(long storeUserId, LocalDate date) {
            WorkAndBreakTime workAndBreakTime = WorkAndBreakTime.create(
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)), null
            ).getOrThrow();

            return new AddWorkScheduleCommand(storeUserId, date, workAndBreakTime, ofManual());
        }
        
        @Test
        @DisplayName("추가대상 근무자 정보가 캘린더의 근무자 정보와 다르면 예외를 던진다")
        void shouldThrowException_whenStoreUserIdMismatch() {
            List<DateRange> calendarRanges = calendarRangesOfYearMonth(2026, JANUARY);
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser, calendarRanges, List.of());

            long otherUserId = calendarUser + 1;
            AddWorkScheduleCommand cmd = commandWithUserAndScheduleDate(otherUserId, calendarRanges.getFirst().start());

            assertThatThrownBy(() -> calendar.addSchedule(10L, cmd));
        }

        @Test
        @DisplayName("추가할 근무일자가 캘린더에 로드된 날짜범위를 벗어나면 예외를 던진다")
        void shouldThrowException_whenScheduleDateNotLoaded() {
            List<DateRange> calendarRanges = calendarRangesOfYearMonth(2026, JANUARY);
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser, calendarRanges, List.of());

            LocalDate dateOutOfCalendarRanges = calendarRanges.stream()
                    .sorted(comparing(DateRange::end)).toList()
                    .getLast()
                    .end().plusDays(1);
            AddWorkScheduleCommand cmd = commandWithUserAndScheduleDate(calendarUser, dateOutOfCalendarRanges);

            assertThatThrownBy(() -> calendar.addSchedule(10L, cmd));
        }

        @Test
        @DisplayName("기존 스케줄과 근무시간이 겹치면 ScheduleConflict 위반으로 실패한다")
        void shouldReturnFailure_whenWorkTimeOverlaps() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L, existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0)));
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY), List.of(existingSchedule));

            LocalDate otherDate = existingDate.minusDays(1);
            TimeRange conflictWorkTime
                    = new TimeRange(LocalTime.of(23, 0), LocalTime.of(11, 0));
            AddWorkScheduleCommand cmd = new AddWorkScheduleCommand(calendarUser
                    , otherDate
                    , workAndBreakTimeWithoutBreak(conflictWorkTime)
                    , ofManual()
            );
            Result<WorkSchedule, CreateAndChange> result = calendar.addSchedule(2L, cmd);

            assertThat(result.isFailure()).isTrue();
            CreateAndChange violation = result.getErrorOrThrow();
            assertThat(violation).isInstanceOf(ScheduleConflict.class);
            assertThat(((ScheduleConflict)violation).existingScheduleId()).isEqualTo(existingSchedule.getId());
        }

        @Test
        @DisplayName("기존 스케줄과 날짜가 같으면 ScheduleConflict 위반으로 실패한다")
        void shouldReturnFailure_whenSameScheduleDateExists() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY)
                    , List.of(existingSchedule)
            );

            AddWorkScheduleCommand cmd = new AddWorkScheduleCommand(calendarUser
                    , existingDate // same date
                    , workAndBreakTimeWithoutBreak(new TimeRange(LocalTime.of(18, 0), LocalTime.of(19, 0)))
                    , ofManual()
            );
            Result<WorkSchedule, CreateAndChange> result = calendar.addSchedule(11L, cmd);

            assertThat(result.isFailure()).isTrue();
            CreateAndChange violation = result.getErrorOrThrow();
            assertThat(violation).isInstanceOf(ScheduleConflict.class);
            assertThat(((ScheduleConflict)violation).existingScheduleId()).isEqualTo(existingSchedule.getId());
        }

        @Test
        @DisplayName("충돌이 없으면 성공결과를 반환한다.")
        void shouldReturnSuccess_whenNoConflict() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY), List.of(existingSchedule)
            );

            AddWorkScheduleCommand cmd = new AddWorkScheduleCommand(calendarUser
                    , existingDate.plusDays(1)
                    , workAndBreakTimeWithoutBreak(new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0)))
                    , ofManual()
            );
            Result<WorkSchedule, CreateAndChange> result = calendar.addSchedule(2L, cmd);

            assertThat(result.isSuccess()).isTrue();
            WorkSchedule created = result.getOrThrow();
            assertThat(created.getId()).isEqualTo(2L);
            assertThat(created.getScheduleDate()).isEqualTo(cmd.scheduleDate());
        }


        @Test
        @DisplayName("성공한 addSchedule 이후, 같은 날짜로 다시 추가하면 충돌로 실패한다 (내부 컬렉션에 추가됨을 간접 검증)")
        void shouldBeSavedInCalendar_whenAddScheduleSucceeds() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY)
                    , List.of(existingSchedule)
            );

            AddWorkScheduleCommand cmd = new AddWorkScheduleCommand(calendarUser
                    , existingDate.plusDays(1)
                    , workAndBreakTimeWithoutBreak(new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0)))
                    , ofManual()
            );
            Result<WorkSchedule, CreateAndChange> first = calendar.addSchedule(2L, cmd);
            Result<WorkSchedule, CreateAndChange> second = calendar.addSchedule(3L, cmd);

            assertThat(first.isSuccess()).isTrue();
            assertThat(second.isFailure()).isTrue();
            CreateAndChange violation = second.getErrorOrThrow();
            assertThat(violation).isInstanceOf(ScheduleConflict.class);
            assertThat(((ScheduleConflict)violation).existingScheduleId()).isEqualTo(first.getOrThrow().getId());
        }
    }

    @Nested
    @DisplayName("스케줄 변경 (changeSchedule)")
    class ChangeSchedule {
        @Test
        @DisplayName("변경할 스케줄의 근무자 정보가 캘린더의 근무자 정보와 다르면 예외를 던진다")
        void shouldThrowException_whenStoreUserIdMismatch() {
            List<DateRange> calendarRanges = calendarRangesOfYearMonth(2026, JANUARY);
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser, calendarRanges, List.of());

            long otherUserId = calendarUser + 1;
            LocalDate validScheduleDate = calendarRanges.getFirst().start();
            WorkAndBreakTime validWorkAndBreakTime = workAndBreakTimeWithoutBreak(new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)));
            WorkSchedule workScheduleOfAnotherUser = new WorkSchedule(1L
                    , otherUserId
                    , validScheduleDate, validWorkAndBreakTime, ofManual()
            );

            assertThatThrownBy(() -> calendar.changeSchedule(workScheduleOfAnotherUser, validScheduleDate, validWorkAndBreakTime));
        }

        @Test
        @DisplayName("변경할 근무일자가 캘린더에 로드된 날짜범위를 벗어나면 예외를 던진다")
        void shouldThrowException_whenScheduleDateNotLoaded() {
            List<DateRange> calendarRanges = calendarRangesOfYearMonth(2026, JANUARY);
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser, calendarRanges, List.of());

            WorkSchedule workSchedule = createWorkSchedule(1L
                    , calendarRanges.getFirst().start()
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            LocalDate dateOutOfCalendarRanges = calendarRanges.stream()
                    .sorted(comparing(DateRange::end)).toList()
                    .getLast()
                    .end().plusDays(1);

            assertThatThrownBy(() -> calendar.changeSchedule(workSchedule
                    , dateOutOfCalendarRanges
                    , workSchedule.getWorkAndBreakTime()
            ));
        }

        @Test
        @DisplayName("기존 스케줄과 근무시간이 겹치면 ScheduleConflict 위반으로 실패한다")
        void shouldReturnFailure_whenWorkTimeOverlaps() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkSchedule scheduleToChange = createWorkSchedule(2L
                    , existingDate.minusDays(1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY)
                    , List.of(existingSchedule, scheduleToChange)
            );

            WorkAndBreakTime conflictTime = workAndBreakTimeWithoutBreak(
                    new TimeRange(LocalTime.of(23, 0), LocalTime.of(11, 0))
            );
            Result<Void, CreateAndChange> result = calendar.changeSchedule(scheduleToChange
                    , scheduleToChange.getScheduleDate(), conflictTime);

            assertThat(result.isFailure()).isTrue();
            CreateAndChange violation = result.getErrorOrThrow();
            assertThat(violation).isInstanceOf(ScheduleConflict.class);
            assertThat(((ScheduleConflict) violation).existingScheduleId()).isEqualTo(existingSchedule.getId());
        }

        @Test
        @DisplayName("기존 스케줄과 날짜가 같으면 ScheduleConflict 위반으로 실패한다")
        void shouldReturnFailure_whenSameScheduleDateExists() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkSchedule scheduleToChange = createWorkSchedule(2L
                    , existingDate.minusDays(1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY)
                    , List.of(existingSchedule, scheduleToChange)
            );

            WorkAndBreakTime notOverlappedTime = workAndBreakTimeWithoutBreak(
                    new TimeRange(LocalTime.of(18, 0), LocalTime.of(19, 0))
            );
            Result<Void, CreateAndChange> result = calendar.changeSchedule(scheduleToChange, existingDate, notOverlappedTime);

            assertThat(result.isFailure()).isTrue();
            CreateAndChange violation = result.getErrorOrThrow();
            assertThat(violation).isInstanceOf(ScheduleConflict.class);
            assertThat(((ScheduleConflict) violation).existingScheduleId()).isEqualTo(existingSchedule.getId());
        }

        @Test
        @DisplayName("충돌이 없으면 성공결과를 반환한다.")
        void shouldReturnSuccess_whenNoConflict() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkSchedule scheduleToChange = createWorkSchedule(2L
                    , existingDate.minusDays(1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY)
                    , List.of(existingSchedule, scheduleToChange)
            );

            LocalDate newDate = existingDate.plusDays(1);
            WorkAndBreakTime newTime = workAndBreakTimeWithoutBreak(new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)));
            Result<Void, CreateAndChange> result = calendar.changeSchedule(scheduleToChange, newDate, newTime);

            assertThat(result.isSuccess()).isTrue();
            assertThat(scheduleToChange.getScheduleDate()).isEqualTo(newDate);
            assertThat(scheduleToChange.getWorkAndBreakTime().workTime()).isEqualTo(newTime.workTime());
        }
        @Test
        @DisplayName("성공한 changeSchedule 이후, 같은 날짜로 추가하면 충돌로 실패한다 (내부 컬렉션에 추가됨을 간접 검증)")
        void shouldBeSavedInCalendar_whenChangeScheduleSucceeds() {
            LocalDate existingDate = LocalDate.of(2026, JANUARY, 15);
            WorkSchedule existingSchedule = createWorkSchedule(1L
                    , existingDate
                    , new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0))
            );
            WorkSchedule scheduleToChange = createWorkSchedule(2L
                    , existingDate.minusDays(1)
                    , new TimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0))
            );
            WorkScheduleCalendar calendar = WorkScheduleCalendar.of(calendarUser
                    , calendarRangesOfYearMonth(2026, JANUARY)
                    , List.of(existingSchedule, scheduleToChange)
            );

            LocalDate newDate = existingDate.plusDays(1);
            WorkAndBreakTime newTime = workAndBreakTimeWithoutBreak(new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)));
            Result<Void, CreateAndChange> result = calendar.changeSchedule(scheduleToChange, newDate, newTime);
            assertThat(result.isSuccess()).isTrue();

            AddWorkScheduleCommand conflictCmd = new AddWorkScheduleCommand(calendarUser, newDate, newTime, ofManual());
            Result<WorkSchedule, CreateAndChange> conflictResult = calendar.addSchedule(3L, conflictCmd);

            assertThat(conflictResult.isFailure()).isTrue();
            CreateAndChange violation = conflictResult.getErrorOrThrow();
            assertThat(violation).isInstanceOf(ScheduleConflict.class);
            assertThat(((ScheduleConflict) violation).existingScheduleId()).isEqualTo(scheduleToChange.getId());
        }
    }
}
