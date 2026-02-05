package com.workus.workus.payroll.employee.domain.model;

/**
 * 사회보험 종류
 * 
 * 적용 대상:
 * - 국민연금: 만 18세 이상 60세 미만 근로자 (1인 이상 사업장)
 * - 건강보험/장기요양보험: 모든 상용근로자 (1인 이상 사업장)
 * - 고용보험: 1개월 이상 근로계약을 체결한 상용근로자
 * - 산재보험: 모든 근로자 (사업주 포함)
 * 
 * 비적용 대상:
 * - 국민연금: 60세 이상 신규 입사자, 일용근로자(1개월 미만), 외국인 중 협정국 미해당자
 * - 건강보험/장기요양보험: 일용근로자(1개월 미만), 외국인 중 체류자격 제한 대상
 * - 고용보험: 1개월 미만 근로자, 주 15시간 미만 단시간 근로자
 * - 산재보험: 적용 제외 없음 (전 근로자 의무가입)
 * 
 * TODO: 외국인 적용 제외 판단 시 국적별 협정국 여부, 체류자격 등 상세 조건 추가 필요
 */
public enum SocialInsuranceType {
    NATIONAL_PENSION("국민연금") {
        @Override
        public boolean isEligible(int age, int contractMonths) {
            return age >= 18 && age < 60;
        }

        @Override
        public boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
            // 60세 이상 신규 입사자
            if (age >= 60) return true;
            // 일용근로자 (1개월 미만 고용)
            if (contractMonths < 1) return true;
            // 외국인 중 협정국 미해당자
            if (nationality.isForeigner() && !nationality.isPensionTreatyCountry()) return true;
            return false;
        }
    },
    HEALTH_INSURANCE("건강보험") {
        @Override
        public boolean isEligible(int age, int contractMonths) {
            return true;
        }

        @Override
        public boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
            // 일용근로자 (1개월 미만 고용)
            if (contractMonths < 1) return true;
            // 외국인 중 체류자격 제한 대상
            if (nationality.isForeigner() && !nationality.isHealthInsuranceEligible()) return true;
            return false;
        }
    },
    LONG_TERM_CARE_INSURANCE("장기요양보험") {
        @Override
        public boolean isEligible(int age, int contractMonths) {
            return true;
        }

        @Override
        public boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
            // 일용근로자 (1개월 미만 고용)
            if (contractMonths < 1) return true;
            // 외국인 중 체류자격 제한 대상
            if (nationality.isForeigner() && !nationality.isHealthInsuranceEligible()) return true;
            return false;
        }
    },
    EMPLOYMENT_INSURANCE("고용보험") {
        @Override
        public boolean isEligible(int age, int contractMonths) {
            return contractMonths >= 1;
        }

        @Override
        public boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
            // 1개월 미만 근로자
            if (contractMonths < 1) return true;
            // 주 15시간 미만 단시간 근로자 (4주 평균 기준)
            if (weeklyWorkHours < 15) return true;
            return false;
        }
    },
    INDUSTRIAL_ACCIDENT_INSURANCE("산재보험") {
        @Override
        public boolean isEligible(int age, int contractMonths) {
            return true;
        }

        @Override
        public boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
            // 적용 제외 없음 (전 근로자 의무가입)
            return false;
        }
    };

    private final String description;

    SocialInsuranceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 사회보험 적용 대상 여부 판단
     * 
     * @param age 근로자 나이 (만 나이)
     * @param contractMonths 근로계약 기간 (개월)
     * @return 적용 대상 여부
     */
    public abstract boolean isEligible(int age, int contractMonths);

    /**
     * 사회보험 비적용 대상 여부 판단
     * 
     * @param age 근로자 나이 (만 나이)
     * @param contractMonths 근로계약 기간 (개월)
     * @param weeklyWorkHours 주당 근무시간 (4주 평균)
     * @param nationality 국적
     * @return 비적용 대상 여부
     */
    public abstract boolean isExempt(int age, int contractMonths, double weeklyWorkHours, Nationality nationality);

    /**
     * 사회보험 비적용 대상 여부 판단 (내국인 기준)
     */
    public boolean isExempt(int age, int contractMonths, double weeklyWorkHours) {
        return isExempt(age, contractMonths, weeklyWorkHours, Nationality.KR);
    }

    /**
     * 최종 사회보험 적용 여부 판단
     * 적용 대상이면서 비적용 대상이 아닌 경우 true
     */
    public boolean isApplicable(int age, int contractMonths, double weeklyWorkHours, Nationality nationality) {
        return isEligible(age, contractMonths) && !isExempt(age, contractMonths, weeklyWorkHours, nationality);
    }

    /**
     * 최종 사회보험 적용 여부 판단 (내국인 기준)
     */
    public boolean isApplicable(int age, int contractMonths, double weeklyWorkHours) {
        return isApplicable(age, contractMonths, weeklyWorkHours, Nationality.KR);
    }

    /**
     * 나이만으로 적용 대상 여부 판단 (계약기간 무관한 경우)
     */
    public boolean isEligibleByAge(int age) {
        return isEligible(age, Integer.MAX_VALUE);
    }
}
