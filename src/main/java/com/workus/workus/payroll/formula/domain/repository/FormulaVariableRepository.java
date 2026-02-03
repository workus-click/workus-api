package com.workus.workus.payroll.formula.domain.repository;

import com.workus.workus.payroll.formula.domain.model.FormulaVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FormulaVariableRepository extends JpaRepository<FormulaVariable, Long> {
    /**
     * 특정 매장의 특정 카테고리(지급항목, 공제항목 등)에 해당하는 변수 목록을 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param codeType 코드 타입 (PAY_ITEM, DEDUCT_ITEM 등)
     * @return 변수 목록
     */
    List<FormulaVariable> findByStoreIdAndCodeType(Long storeId, String codeType);

    /**
     * 특정 매장의 특정 카테고리(지급항목, 공제항목 등)에 해당하는 변수 코드 목록을 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param codeType 코드 타입 (PAY_ITEM, DEDUCT_ITEM 등)
     * @return 변수 코드 목록 (Set)
     */
    @Query("SELECT fv.code FROM FormulaVariable fv WHERE fv.storeId = :storeId AND fv.codeType = :codeType")
    Set<String> findCodesByStoreIdAndCodeType(@Param("storeId") Long storeId, @Param("codeType") String codeType);

    /**
     * 특정 매장의 특정 카테고리들에 해당하는 변수 코드 목록을 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param codeTypes 코드 타입 목록
     * @return 변수 코드 목록 (Set)
     */
    @Query("SELECT fv.code FROM FormulaVariable fv WHERE fv.storeId = :storeId AND fv.codeType IN :codeTypes")
    Set<String> findCodesByStoreIdAndCodeTypeIn(@Param("storeId") Long storeId, @Param("codeTypes") List<String> codeTypes);
}
