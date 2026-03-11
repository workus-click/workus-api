package com.workus.workus.store.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workus.workus.store.domain.model.StoreInfo;

public interface JpaStoreInfoRepository extends JpaRepository<StoreInfo, Long> {
    boolean existsByBusinessNumber(String businessNumber);
}
