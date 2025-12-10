package com.workus.workus.schedule.domain.repository;

import com.workus.workus.schedule.domain.model.WorkSchedule;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleRepositoryCustom {
    /** 전/당/익일 스케줄 + 락 (AppService에서 동시성 제어용) */
    List<WorkSchedule> findRangeForUpdate(
            Long storeUserId,
            LocalDate from,
            LocalDate to
    );
}
