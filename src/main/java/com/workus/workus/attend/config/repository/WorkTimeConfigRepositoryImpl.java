package com.workus.workus.attend.config.repository;

import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import com.workus.workus.attend.config.domain.repository.WorkTimeConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkTimeConfigRepositoryImpl implements WorkTimeConfigRepository {
    private final JpaWorkTimeConfigRepository jpaWorkTimeConfigRepository;

    @Override
    public Optional<WorkTimeConfig> findById(Long id) {
        return jpaWorkTimeConfigRepository.findById(id);
    }

    @Override
    public List<WorkTimeConfig> findByStoreId(Long storeId) {
        return jpaWorkTimeConfigRepository.findByStoreId(storeId);
    }

    @Override
    public WorkTimeConfig save(WorkTimeConfig workTimeConfig) {
        return jpaWorkTimeConfigRepository.save(workTimeConfig);
    }

    @Override
    public void delete(WorkTimeConfig workTimeConfig) {
        jpaWorkTimeConfigRepository.delete(workTimeConfig);
    }
}

