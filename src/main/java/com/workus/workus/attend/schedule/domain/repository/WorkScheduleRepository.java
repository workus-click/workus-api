package com.workus.workus.attend.schedule.domain.repository;

import com.workus.workus.attend.schedule.domain.core.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long>, WorkScheduleRepositoryCustom {
}

