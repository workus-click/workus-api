package com.workus.workus.store.domain.model;

import com.workus.workus.common.component.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_info")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreInfo {
    @Id
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "business_no", nullable = false)
    private String businessNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "industry", nullable = false)
    private String businessType;

    @Column(name = "owner_phone", nullable = false)
    private String ownerPhoneNumber;

    @Column(name = "store_zip_code", nullable = false)
    private String storeZipCode;

    @Column(name = "detailed_address", nullable = false)
    private String detailedAddress;

    public static StoreInfo of(
        String storeName,
        String businessNumber,
        String ownerName,
        String businessType,
        String ownerPhoneNumber,
        String storeZipCode,
        String detailedAddress
    ) {
        return StoreInfo.builder()
            .storeId(IdGenerator.nextId())
            .storeName(storeName)
            .businessNumber(businessNumber)
            .ownerName(ownerName)
            .businessType(businessType)
            .ownerPhoneNumber(ownerPhoneNumber)
            .storeZipCode(storeZipCode)
            .detailedAddress(detailedAddress)
            .build();
    }

}
