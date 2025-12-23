package com.workus.workus.schedule.domain.repository;

import com.workus.workus.schedule.domain.core.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long>, WorkScheduleRepositoryCustom {
}

