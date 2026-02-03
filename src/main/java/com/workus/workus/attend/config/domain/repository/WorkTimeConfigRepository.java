package com.workus.workus.attend.config.domain.repository;

import com.workus.workus.attend.config.domain.model.WorkTimeConfig;

import java.util.List;
import java.util.Optional;

public interface WorkTimeConfigRepository {
    Optional<WorkTimeConfig> findById(Long id);
    List<WorkTimeConfig> findByStoreId(Long storeId);
    WorkTimeConfig save(WorkTimeConfig workTimeConfig);
    void delete(WorkTimeConfig workTimeConfig);
}

