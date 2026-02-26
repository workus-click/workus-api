package com.workus.workus.store.domain.repository;

import com.workus.workus.store.domain.model.StoreInfo;

public interface StoreInfoRepository {
    boolean existsByBusinessNumber(String businessNumber);

    void save(StoreInfo storeInfo);
}
