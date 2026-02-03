-- =====================================================
-- V4: 급여 계산식 테이블 추가 및 WorkTimeConfig 테이블 수정
-- =====================================================

-- =====================================================
-- 1. work_time_config 테이블 수정
-- =====================================================
-- 엔티티와 매핑하기 위해 worker_type을 VARCHAR로 변경 (EmployeeType ENUM 저장)
-- 휴게시간은 선택사항이므로 NULL 허용으로 변경

-- worker_type 컬럼 타입 변경 (CHAR(1) -> VARCHAR(20))
ALTER TABLE work_time_config
    MODIFY COLUMN worker_type VARCHAR(20) NOT NULL COMMENT '알바/직원구분 (PART_TIME: 알바, FULL_TIME: 직원)';

-- 휴게시간 NULL 허용으로 변경
ALTER TABLE work_time_config
    MODIFY COLUMN break_start_time TIME NULL COMMENT '휴게시작시간';

ALTER TABLE work_time_config
    MODIFY COLUMN break_end_time TIME NULL COMMENT '휴게종료시간';

-- work_time_code 컬럼 삭제 (엔티티에서 사용하지 않음)
-- 기존 unique 제약조건 삭제 후 컬럼 삭제
ALTER TABLE work_time_config
    DROP INDEX store_id;

ALTER TABLE work_time_config
    DROP COLUMN work_time_code;

-- =====================================================
-- 2. salary_calculation_formula 테이블 생성 (Immutable)
-- =====================================================
CREATE TABLE IF NOT EXISTS salary_calculation_formula (
    formula_id BIGINT NOT NULL COMMENT '계산식 고유 ID',
    store_id BIGINT NOT NULL COMMENT '매장 아이디',
    formula_type VARCHAR(50) NOT NULL COMMENT '계산식 타입 (BASE_SALARY, INCOME_TAX 등)',
    formula TEXT NOT NULL COMMENT '계산식 (예: WORK_HOURS * HOURLY_RATE)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (formula_id),
    INDEX idx_salary_formula_store (store_id),
    INDEX idx_salary_formula_type (store_id, formula_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='급여 계산식 (불변 - INSERT만 가능)';

-- =====================================================
-- 3. salary_calculation_formula_version 테이블 생성
-- =====================================================
CREATE TABLE IF NOT EXISTS salary_calculation_formula_version (
    version_id BIGINT NOT NULL COMMENT '버전 고유 ID',
    store_id BIGINT NOT NULL COMMENT '매장 아이디',
    version_number INT NOT NULL COMMENT '버전 번호',

    -- 지급항목 (PayItem) - 8개
    base_salary_formula_id BIGINT NULL COMMENT '기본급 계산식 ID',
    overtime_allowance_formula_id BIGINT NULL COMMENT '연장수당 계산식 ID',
    night_shift_allowance_formula_id BIGINT NULL COMMENT '야간수당 계산식 ID',
    holiday_allowance_formula_id BIGINT NULL COMMENT '휴일수당 계산식 ID',
    other_earnings_formula_id BIGINT NULL COMMENT '기타소득 계산식 ID',
    bonus_pay_formula_id BIGINT NULL COMMENT '상여금 계산식 ID',
    general_allowance_formula_id BIGINT NULL COMMENT '일반수당 계산식 ID',
    weekly_holiday_allowance_formula_id BIGINT NULL COMMENT '주휴수당 계산식 ID',

    -- 공제항목 (DeductItem) - 8개
    income_tax_formula_id BIGINT NULL COMMENT '소득세 계산식 ID',
    local_income_tax_formula_id BIGINT NULL COMMENT '지방소득세 계산식 ID',
    resident_tax_formula_id BIGINT NULL COMMENT '주민세 계산식 ID',
    national_pension_formula_id BIGINT NULL COMMENT '국민연금 계산식 ID',
    health_insurance_formula_id BIGINT NULL COMMENT '건강보험 계산식 ID',
    long_term_care_insurance_formula_id BIGINT NULL COMMENT '장기요양보험 계산식 ID',
    employment_insurance_formula_id BIGINT NULL COMMENT '고용보험 계산식 ID',
    other_deduct_items_formula_id BIGINT NULL COMMENT '기타공제 계산식 ID',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',

    PRIMARY KEY (version_id),
    UNIQUE KEY uk_store_version (store_id, version_number),
    INDEX idx_formula_version_store (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='급여 계산식 버전 관리';
