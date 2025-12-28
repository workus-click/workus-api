package com.workus.workus.schedule.infra.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workus.workus.schedule.domain.core.WorkSchedule;
import com.workus.workus.schedule.domain.repository.WorkScheduleRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkScheduleRepositoryImpl implements WorkScheduleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

}
