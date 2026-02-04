-- 사회보험 요율 테이블
-- 조회 전용 참조 데이터로, 연 1회 요율 변경 시 SQL로 직접 입력
-- AUTO_INCREMENT 사용: 사용자가 유연하게 추가하는 데이터가 아니므로 Flyway SQL로 관리
CREATE TABLE IF NOT EXISTS social_insurance_rates (
    social_insurance_rate_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year CHAR(4) NOT NULL COMMENT '적용연도',
    insurance_type VARCHAR(20) NOT NULL COMMENT '보험종류 (PENSION, HEALTH, LONGCARE, EMPLOY)',
    effective_from DATE NOT NULL COMMENT '적용시작일',
    effective_to DATE NOT NULL COMMENT '적용종료일',
    employee_rate DECIMAL(6,4) NOT NULL COMMENT '사원부담 요율',
    employer_rate DECIMAL(6,4) NOT NULL COMMENT '회사부담 요율',
    total_rate DECIMAL(6,4) NOT NULL COMMENT '전체 요율',
    min_base_amount BIGINT NULL COMMENT '기준소득 하한액 (원)',
    max_base_amount BIGINT NULL COMMENT '기준소득 상한액 (원)',
    UNIQUE KEY uk_social_insurance_rates (year, insurance_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사회보험 요율';
