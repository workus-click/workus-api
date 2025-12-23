package com.workus.workus.schedule.domain.repository;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.schedule.domain.core.TimeRange;
import com.workus.workus.schedule.domain.core.WorkSchedule;
import com.workus.workus.schedule.domain.core.WorkScheduleSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WorkScheduleRepositoryTest {

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Test
    void save_and_find() {
        // given
        Long storeUserId = IdGenerator.nextId();
        WorkSchedule schedule = new WorkSchedule(
                1L,
                storeUserId,
                LocalDate.now(),                                  // storeUserId
                new TimeRange(LocalTime.of(5, 0), LocalTime.of(14, 0)), // scheduleDate
                new TimeRange(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                WorkScheduleSource.MANUAL,
                null
        );
        // when: 저장
        workScheduleRepository.save(schedule);

        // then: 조회
        WorkSchedule found = workScheduleRepository
                .findById(schedule.getId())
                .orElseThrow();

        assertEquals(schedule, found);
    }

}