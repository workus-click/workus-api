package com.workus.workus.sample.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workus.workus.sample.domain.QTestEntity;
import com.workus.workus.sample.domain.TestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SampleService {
    private final JPAQueryFactory jpaQueryFactory;
    public List<TestEntity> test(){
        return jpaQueryFactory.selectFrom(QTestEntity.testEntity).fetch();
    }
}
