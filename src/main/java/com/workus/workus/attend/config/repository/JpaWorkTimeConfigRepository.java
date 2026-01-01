package com.workus.workus.attend.config.repository;

import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaWorkTimeConfigRepository extends JpaRepository<WorkTimeConfig, Long> {
    List<WorkTimeConfig> findByStoreId(Long storeId);
}

