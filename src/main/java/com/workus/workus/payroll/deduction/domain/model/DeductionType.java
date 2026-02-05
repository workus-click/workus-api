package com.workus.workus.payroll.deduction.domain.model;

import com.workus.workus.payroll.employee.domain.model.Nationality;
import lombok.Getter;

/**
 * 공제 항목 타입
 * - 4대보험 + 소득세/지방소득세 통합 관리
 * - 계산 순서(order)에 따라 순차 계산 (지방소득세는 소득세 이후, 장기요양은 건강보험 이후)
 * 
 * 적용 대상:
 * - 국민연금: 만 18세 이상 60세 미만, 1개월 이상 계약
 * - 건강보험/장기요양보험: 1개월 이상 계약
 * - 고용보험: 1개월 이상 계약, 주 15시간 이상
 * - 소득세/지방소득세: 과세소득이 있는 모든 근로자
 * 
 * 비적용 대상:
 * - 국민연금: 60세 이상, 1개월 미만 계약, 외국인 협정국 미해당
 * - 건강보험/장기요양: 1개월 미만 계약, 외국인 체류자격 제한
 * - 고용보험: 1개월 미만 계약, 주 15시간 미만
 */
@Getter
public enum DeductionType {
    // 4대보험
    NATIONAL_PENSION(1, "국민연금", DeductionCategory.SOCIAL_INSURANCE),
    HEALTH_INSURANCE(2, "건강보험", DeductionCategory.SOCIAL_INSURANCE),
    LONG_TERM_CARE_INSURANCE(3, "장기요양보험", DeductionCategory.SOCIAL_INSURANCE),
    EMPLOYMENT_INSURANCE(4, "고용보험", DeductionCategory.SOCIAL_INSURANCE),
    
    // 세금
    INCOME_TAX(5, "소득세", DeductionCategory.TAX),
    LOCAL_INCOME_TAX(6, "지방소득세", DeductionCategory.TAX);

    private final int order;
    private final String description;
    private final DeductionCategory category;

    DeductionType(int order, String description, DeductionCategory category) {
        this.order = order;
        this.description = description;
        this.category = category;
    }

    /**
     * 사회보험 여부
     */
    public boolean isSocialInsurance() {
        return category == DeductionCategory.SOCIAL_INSURANCE;
    }

    /**
     * 세금 여부
     */
    public boolean isTax() {
        return category == DeductionCategory.TAX;
    }

    /**
     * 적용 대상 여부 판단
     */
    public boolean isEligible(int age, int contractMonths) {
        return switch (this) {
            case NATIONAL_PENSION -> age >= 18 && age < 60 && contractMonths >= 1;
            case HEALTH_INSURANCE, LONG_TERM_CARE_INSURANCE -> contractMonths >= 1;
            case EMPLOYMENT_INSURANCE -> contractMonths >= 1;
            case INCOME_TAX, LOCAL_INCOME_TAX -> true;  // 과세소득 있으면 적용
        };
    }

    /**
     * 비적용 대상 여부 판단
     */
    public boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
        return switch (this) {
            case NATIONAL_PENSION -> {
                if (age >= 60) yield true;
                if (contractMonths < 1) yield true;
                if (nationality.isForeigner() && !nationality.isPensionTreatyCountry()) yield true;
                yield false;
            }
            case HEALTH_INSURANCE, LONG_TERM_CARE_INSURANCE -> {
                if (contractMonths < 1) yield true;
                if (nationality.isForeigner() && !nationality.isHealthInsuranceEligible()) yield true;
                yield false;
            }
            case EMPLOYMENT_INSURANCE -> {
                if (contractMonths < 1) yield true;
                if (weeklyWorkHours < 15) yield true;
                yield false;
            }
            case INCOME_TAX, LOCAL_INCOME_TAX -> false;  // 비적용 없음
        };
    }

    /**
     * 비적용 대상 여부 (내국인 기준)
     */
    public boolean isExempt(int age, int contractMonths, double weeklyWorkHours) {
        return isExempt(age, contractMonths, weeklyWorkHours, Nationality.KR);
    }

    /**
     * 최종 적용 여부
     */
    public boolean isApplicable(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
        return isEligible(age, contractMonths) && !isExempt(age, contractMonths, weeklyWorkHours, nationality);
    }

    /**
     * 최종 적용 여부 (내국인 기준)
     */
    public boolean isApplicable(int age, int contractMonths, double weeklyWorkHours) {
        return isApplicable(age, contractMonths, weeklyWorkHours, Nationality.KR);
    }

    /**
     * 순서대로 정렬된 공제 항목 목록
     */
    public static DeductionType[] orderedValues() {
        DeductionType[] values = values();
        java.util.Arrays.sort(values, java.util.Comparator.comparingInt(DeductionType::getOrder));
        return values;
    }

    /**
     * 사회보험만 필터링
     */
    public static DeductionType[] socialInsuranceTypes() {
        return java.util.Arrays.stream(values())
                .filter(DeductionType::isSocialInsurance)
                .sorted(java.util.Comparator.comparingInt(DeductionType::getOrder))
                .toArray(DeductionType[]::new);
    }

    /**
     * 세금만 필터링
     */
    public static DeductionType[] taxTypes() {
        return java.util.Arrays.stream(values())
                .filter(DeductionType::isTax)
                .sorted(java.util.Comparator.comparingInt(DeductionType::getOrder))
                .toArray(DeductionType[]::new);
    }
}
