package com.workus.workus.store.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.workus.workus.store.domain.model.StoreInfo;
import com.workus.workus.store.domain.repository.StoreInfoRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreInfoRepositoryImpl implements StoreInfoRepository {
    private final JpaStoreInfoRepository jpaStoreInfoRepository;

    @Override
    public boolean existsByBusinessNumber(String businessNumber) {
        return jpaStoreInfoRepository.existsByBusinessNumber(businessNumber);
    }

    @Override
    public void save(StoreInfo storeInfo) {
        jpaStoreInfoRepository.save(storeInfo);
    }
}
