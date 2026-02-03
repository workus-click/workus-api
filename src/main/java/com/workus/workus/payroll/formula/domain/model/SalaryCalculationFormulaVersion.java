package com.workus.workus.payroll.formula.domain.model;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.common.entity.BaseEntity;
import com.workus.workus.payroll.formula.domain.model.DeductItemFormulaType;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.model.PayItemFormulaType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

/**
 * 급여 계산식 버전
 * - 계산식이 추가되거나 삭제될 때마다 새로운 버전이 생성됨
 * - 각 FormulaType별로 하나의 계산식 ID를 저장
 */
@Entity
@Table(name = "salary_calculation_formula_version",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_store_version",
           columnNames = {"store_id", "version_number"}
       ))
@Getter
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SalaryCalculationFormulaVersion extends BaseEntity {
    @Id
    @Column(name = "version_id")
    private Long id;

    @Column(name = "store_id", nullable = false, updatable = false)
    private Long storeId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    // ========== 지급항목 (PayItem) ==========
    @Column(name = "base_salary_formula_id")
    private Long baseSalaryFormulaId;

    @Column(name = "overtime_allowance_formula_id")
    private Long overtimeAllowanceFormulaId;

    @Column(name = "night_shift_allowance_formula_id")
    private Long nightShiftAllowanceFormulaId;

    @Column(name = "holiday_allowance_formula_id")
    private Long holidayAllowanceFormulaId;

    @Column(name = "other_earnings_formula_id")
    private Long otherEarningsFormulaId;

    @Column(name = "bonus_pay_formula_id")
    private Long bonusPayFormulaId;

    @Column(name = "general_allowance_formula_id")
    private Long generalAllowanceFormulaId;

    @Column(name = "weekly_holiday_allowance_formula_id")
    private Long weeklyHolidayAllowanceFormulaId;

    // ========== 공제항목 (DeductItem) ==========
    @Column(name = "income_tax_formula_id")
    private Long incomeTaxFormulaId;

    @Column(name = "local_income_tax_formula_id")
    private Long localIncomeTaxFormulaId;

    @Column(name = "resident_tax_formula_id")
    private Long residentTaxFormulaId;

    @Column(name = "national_pension_formula_id")
    private Long nationalPensionFormulaId;

    @Column(name = "health_insurance_formula_id")
    private Long healthInsuranceFormulaId;

    @Column(name = "long_term_care_insurance_formula_id")
    private Long longTermCareInsuranceFormulaId;

    @Column(name = "employment_insurance_formula_id")
    private Long employmentInsuranceFormulaId;

    @Column(name = "other_deduct_items_formula_id")
    private Long otherDeductItemsFormulaId;

    /**
     * 첫 번째 버전 생성
     */
    public static SalaryCalculationFormulaVersion createFirstVersion(Long storeId) {
        return SalaryCalculationFormulaVersion.builder()
                .id(IdGenerator.nextId())
                .storeId(storeId)
                .versionNumber(1)
                .build();
    }

    /**
     * 현재 버전을 기반으로 새 버전 생성 (모든 계산식 ID 복사)
     */
    public SalaryCalculationFormulaVersion createNextVersion() {
        return SalaryCalculationFormulaVersion.builder()
                .id(IdGenerator.nextId())
                .storeId(this.storeId)
                .versionNumber(this.versionNumber + 1)
                // 지급항목
                .baseSalaryFormulaId(this.baseSalaryFormulaId)
                .overtimeAllowanceFormulaId(this.overtimeAllowanceFormulaId)
                .nightShiftAllowanceFormulaId(this.nightShiftAllowanceFormulaId)
                .holidayAllowanceFormulaId(this.holidayAllowanceFormulaId)
                .otherEarningsFormulaId(this.otherEarningsFormulaId)
                .bonusPayFormulaId(this.bonusPayFormulaId)
                .generalAllowanceFormulaId(this.generalAllowanceFormulaId)
                .weeklyHolidayAllowanceFormulaId(this.weeklyHolidayAllowanceFormulaId)
                // 공제항목
                .incomeTaxFormulaId(this.incomeTaxFormulaId)
                .localIncomeTaxFormulaId(this.localIncomeTaxFormulaId)
                .residentTaxFormulaId(this.residentTaxFormulaId)
                .nationalPensionFormulaId(this.nationalPensionFormulaId)
                .healthInsuranceFormulaId(this.healthInsuranceFormulaId)
                .longTermCareInsuranceFormulaId(this.longTermCareInsuranceFormulaId)
                .employmentInsuranceFormulaId(this.employmentInsuranceFormulaId)
                .otherDeductItemsFormulaId(this.otherDeductItemsFormulaId)
                .build();
    }

    /**
     * 특정 타입의 계산식 ID 조회
     */
    public Long getFormulaId(FormulaType formulaType) {
        if (formulaType instanceof PayItemFormulaType payType) {
            return switch (payType) {
                case BASE_SALARY -> baseSalaryFormulaId;
                case OVERTIME_ALLOWANCE -> overtimeAllowanceFormulaId;
                case NIGHT_SHIFT_ALLOWANCE -> nightShiftAllowanceFormulaId;
                case HOLIDAY_ALLOWANCE -> holidayAllowanceFormulaId;
                case OTHER_EARNINGS -> otherEarningsFormulaId;
                case BONUS_PAY -> bonusPayFormulaId;
                case GENERAL_ALLOWANCE -> generalAllowanceFormulaId;
                case WEEKLY_HOLIDAY_ALLOWANCE -> weeklyHolidayAllowanceFormulaId;
            };
        } else if (formulaType instanceof DeductItemFormulaType deductType) {
            return switch (deductType) {
                case INCOME_TAX -> incomeTaxFormulaId;
                case LOCAL_INCOME_TAX -> localIncomeTaxFormulaId;
                case RESIDENT_TAX -> residentTaxFormulaId;
                case NATIONAL_PENSION -> nationalPensionFormulaId;
                case HEALTH_INSURANCE -> healthInsuranceFormulaId;
                case LONG_TERM_CARE_INSURANCE -> longTermCareInsuranceFormulaId;
                case EMPLOYMENT_INSURANCE -> employmentInsuranceFormulaId;
                case OTHER_DEDUCT_ITEMS -> otherDeductItemsFormulaId;
            };
        }
        return null;
    }

    /**
     * 특정 타입의 계산식 ID 설정
     */
    public void setFormulaId(FormulaType formulaType, Long formulaId) {
        if (formulaType instanceof PayItemFormulaType payType) {
            switch (payType) {
                case BASE_SALARY -> this.baseSalaryFormulaId = formulaId;
                case OVERTIME_ALLOWANCE -> this.overtimeAllowanceFormulaId = formulaId;
                case NIGHT_SHIFT_ALLOWANCE -> this.nightShiftAllowanceFormulaId = formulaId;
                case HOLIDAY_ALLOWANCE -> this.holidayAllowanceFormulaId = formulaId;
                case OTHER_EARNINGS -> this.otherEarningsFormulaId = formulaId;
                case BONUS_PAY -> this.bonusPayFormulaId = formulaId;
                case GENERAL_ALLOWANCE -> this.generalAllowanceFormulaId = formulaId;
                case WEEKLY_HOLIDAY_ALLOWANCE -> this.weeklyHolidayAllowanceFormulaId = formulaId;
            }
        } else if (formulaType instanceof DeductItemFormulaType deductType) {
            switch (deductType) {
                case INCOME_TAX -> this.incomeTaxFormulaId = formulaId;
                case LOCAL_INCOME_TAX -> this.localIncomeTaxFormulaId = formulaId;
                case RESIDENT_TAX -> this.residentTaxFormulaId = formulaId;
                case NATIONAL_PENSION -> this.nationalPensionFormulaId = formulaId;
                case HEALTH_INSURANCE -> this.healthInsuranceFormulaId = formulaId;
                case LONG_TERM_CARE_INSURANCE -> this.longTermCareInsuranceFormulaId = formulaId;
                case EMPLOYMENT_INSURANCE -> this.employmentInsuranceFormulaId = formulaId;
                case OTHER_DEDUCT_ITEMS -> this.otherDeductItemsFormulaId = formulaId;
            }
        }
    }

    /**
     * 특정 타입에 계산식이 설정되어 있는지 확인
     */
    public boolean hasFormula(FormulaType formulaType) {
        return getFormulaId(formulaType) != null;
    }

    /**
     * 모든 계산식 ID 목록 반환 (null 제외)
     */
    public java.util.List<Long> getAllFormulaIds() {
        java.util.List<Long> ids = new java.util.ArrayList<>();
        // 지급항목
        if (baseSalaryFormulaId != null) ids.add(baseSalaryFormulaId);
        if (overtimeAllowanceFormulaId != null) ids.add(overtimeAllowanceFormulaId);
        if (nightShiftAllowanceFormulaId != null) ids.add(nightShiftAllowanceFormulaId);
        if (holidayAllowanceFormulaId != null) ids.add(holidayAllowanceFormulaId);
        if (otherEarningsFormulaId != null) ids.add(otherEarningsFormulaId);
        if (bonusPayFormulaId != null) ids.add(bonusPayFormulaId);
        if (generalAllowanceFormulaId != null) ids.add(generalAllowanceFormulaId);
        if (weeklyHolidayAllowanceFormulaId != null) ids.add(weeklyHolidayAllowanceFormulaId);
        // 공제항목
        if (incomeTaxFormulaId != null) ids.add(incomeTaxFormulaId);
        if (localIncomeTaxFormulaId != null) ids.add(localIncomeTaxFormulaId);
        if (residentTaxFormulaId != null) ids.add(residentTaxFormulaId);
        if (nationalPensionFormulaId != null) ids.add(nationalPensionFormulaId);
        if (healthInsuranceFormulaId != null) ids.add(healthInsuranceFormulaId);
        if (longTermCareInsuranceFormulaId != null) ids.add(longTermCareInsuranceFormulaId);
        if (employmentInsuranceFormulaId != null) ids.add(employmentInsuranceFormulaId);
        if (otherDeductItemsFormulaId != null) ids.add(otherDeductItemsFormulaId);
        return ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryCalculationFormulaVersion that = (SalaryCalculationFormulaVersion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}