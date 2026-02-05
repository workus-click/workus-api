package com.workus.workus.payroll.employee.domain.model;

/**
 * 국적 (ISO 3166-1 alpha-2 코드 기반)
 * 
 * TODO: common 패키지로 이동 고민 중
 * TODO: 추후 외국인 근로자 지원 시 국가 추가 필요
 * TODO: 협정국 여부 판단 로직 추가 필요 (국민연금 협정국, 건강보험 체류자격 등)
 */
public enum Nationality {
    // 내국인
    KR,
    // 외국인 근로자 주요 출신국
    CN, VN, PH, ID, TH, UZ, KH, NP, MM, LK, BD, PK, MN,
    // 국민연금 협정국 (주요)
    US, CA, DE, FR, GB, AU, JP, IT, NL, BE, PL, HU, CZ, SK, AT, ES, RO, BG, TR, IN, BR, RU;

    // TODO: 필요 시 추가 국가 확장

    /**
     * ISO 3166-1 alpha-2 코드로 Nationality 조회
     * @param code ISO 3166-1 alpha-2 코드 (예: "KR", "US")
     * @return Nationality enum
     * @throws IllegalArgumentException 존재하지 않는 코드인 경우
     */
    public static Nationality fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("국가 코드는 필수입니다.");
        }
        try {
            return Nationality.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 국가 코드입니다: " + code);
        }
    }

    /**
     * ISO 3166-1 alpha-2 코드 반환
     */
    public String getAlpha2Code() {
        return this.name();
    }

    /**
     * 한글 국가명
     */
    public String getKoreanName() {
        return switch (this) {
            case KR -> "대한민국";
            case CN -> "중국";
            case VN -> "베트남";
            case PH -> "필리핀";
            case ID -> "인도네시아";
            case TH -> "태국";
            case UZ -> "우즈베키스탄";
            case KH -> "캄보디아";
            case NP -> "네팔";
            case MM -> "미얀마";
            case LK -> "스리랑카";
            case BD -> "방글라데시";
            case PK -> "파키스탄";
            case MN -> "몽골";
            case US -> "미국";
            case CA -> "캐나다";
            case DE -> "독일";
            case FR -> "프랑스";
            case GB -> "영국";
            case AU -> "호주";
            case JP -> "일본";
            case IT -> "이탈리아";
            case NL -> "네덜란드";
            case BE -> "벨기에";
            case PL -> "폴란드";
            case HU -> "헝가리";
            case CZ -> "체코";
            case SK -> "슬로바키아";
            case AT -> "오스트리아";
            case ES -> "스페인";
            case RO -> "루마니아";
            case BG -> "불가리아";
            case TR -> "튀르키예";
            case IN -> "인도";
            case BR -> "브라질";
            case RU -> "러시아";
        };
    }

    /**
     * 내국인 여부
     */
    public boolean isDomestic() {
        return this == KR;
    }

    /**
     * 외국인 여부
     */
    public boolean isForeigner() {
        return this != KR;
    }

    /**
     * 국민연금 협정국 여부
     * TODO: 협정국 목록 정확히 확인 후 업데이트 필요
     */
    public boolean isPensionTreatyCountry() {
        if (isDomestic()) return true;
        
        // 국민연금 사회보장협정 체결국 (2024년 기준, 확인 필요)
        return switch (this) {
            case US, CA, DE, FR, GB, AU, JP, IT, NL, BE, 
                 PL, HU, CZ, SK, AT, ES, RO, BG, TR, IN, BR -> true;
            default -> false;
        };
    }

    /**
     * 건강보험 적용 가능 체류자격 여부
     * TODO: 체류자격별 적용 조건 확인 후 구현 필요 (현재는 내국인만 true)
     */
    public boolean isHealthInsuranceEligible() {
        if (isDomestic()) return true;
        // TODO: 체류자격별 조건 확인 후 구현
        // 외국인의 경우 체류자격(비자 종류)에 따라 달라짐
        return false;
    }
}
