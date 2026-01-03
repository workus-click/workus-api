package com.workus.workus.attend.schedule.infra.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workus.workus.attend.common.vo.DateRange;
import com.workus.workus.attend.schedule.domain.core.WorkSchedule;
import com.workus.workus.attend.schedule.domain.core.WorkScheduleCalendar;
import com.workus.workus.attend.schedule.domain.repository.WorkScheduleCalendarLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.workus.workus.attend.schedule.domain.core.QWorkSchedule.workSchedule;

@Repository
@RequiredArgsConstructor
public class WorkScheduleCalendarLoaderImpl implements WorkScheduleCalendarLoader {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public WorkScheduleCalendar loadCalendar(Long storeUserId, DateRange loadRange) {
        return loadCalendar(storeUserId, List.of(loadRange));
    }

    @Override
    public WorkScheduleCalendar loadCalendar(Long storeUserId, List<DateRange> loadRanges) {
        BooleanBuilder scheduleDateBetween = new BooleanBuilder();
        for (DateRange range : loadRanges) {
            scheduleDateBetween.or(
                workSchedule.scheduleDate.between(
                    range.from(),
                    range.to()
                )
            );
        }

        List<WorkSchedule> schedules = jpaQueryFactory.selectFrom(workSchedule).where(
                workSchedule.storeUserId.eq(storeUserId)
                , scheduleDateBetween
        ).fetch();

        return WorkScheduleCalendar.of(storeUserId, loadRanges, schedules);
    }


}
