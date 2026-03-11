package com.workus.workus.store.domain.repository;

import java.util.Optional;

import com.workus.workus.store.domain.model.StoreInfo;

public interface StoreInfoRepository {
    boolean existsByBusinessNumber(String businessNumber);

    void save(StoreInfo storeInfo);

    Optional<StoreInfo> findByStoreId(Long storeId);
}
