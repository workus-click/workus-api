package com.workus.workus.store.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workus.workus.store.domain.model.StoreUser;

public interface JpaStoreUserRepository extends JpaRepository<StoreUser, Long> {
    boolean existsByStoreIdAndUserId(Long storeId, Long userId);

    @Query(value = """
        SELECT su.store_id
        FROM store_user su
        JOIN store_info si ON si.store_id = su.store_id
        WHERE su.user_id = :userId
        ORDER BY si.store_name
        LIMIT 1
        """, nativeQuery = true)
    Optional<Long> findFirstStoreIdByUserId(@Param("userId") Long userId);
}
