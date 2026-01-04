package com.workus.workus.attend.schedule.domain.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkScheduleSourceTest {

    @Test
    @DisplayName("자동 스케줄은 연결 근무시간설정이 있으면 생성된다")
    void ofAuto_shouldAllowCreation_whenNonNullWorkTimeId(){
        Long workTimeId = 123L;
        WorkScheduleSource source = WorkScheduleSource.ofAuto(workTimeId);

        assertEquals(CreationType.AUTO, source.creationType());
        assertEquals(workTimeId, source.workTimeId());
    }
    @Test
    @DisplayName("자동 스케줄은 연결 근무시간설정 없이 생성될 수 없다")
    void ofAuto_shouldRejectCreation_whenWorkTimeIdIsNull() {
        assertThrows(NullPointerException.class, () -> WorkScheduleSource.ofAuto(null));
    }

    @Test
    @DisplayName("수동 스케줄은 연결 근무시간설정을 가지지 않는다.")
    void manualSource_notHaveWorkTimeId() {
        WorkScheduleSource source = WorkScheduleSource.ofManual();

        assertEquals(CreationType.MANUAL, source.creationType());
        assertNull(source.workTimeId());
    }

}
