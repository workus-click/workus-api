package com.workus.workus.attend.schedule.infra.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkScheduleRepositoryImpl implements WorkScheduleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

}
