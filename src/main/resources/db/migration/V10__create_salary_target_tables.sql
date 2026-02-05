-- 급여 계산 결과 메인 테이블
CREATE TABLE IF NOT EXISTS salary_target (
    salary_target_id BIGINT NOT NULL COMMENT '급여 대상 ID',
    store_user_id BIGINT NOT NULL COMMENT '매장-유저 ID',
    accrual_start_date DATE NOT NULL COMMENT '귀속 시작일',
    accrual_end_date DATE NOT NULL COMMENT '귀속 종료일',
    pay_cycle VARCHAR(20) DEFAULT NULL COMMENT '급여 주기 (DAILY, WEEKLY, MONTHLY)',
    total_dependents_cnt INT DEFAULT 0 COMMENT '공제대상가족수',
    total_taxable_amount DECIMAL(15,2) DEFAULT 0 COMMENT '과세금액',
    total_deduction_amount DECIMAL(15,2) DEFAULT 0 COMMENT '공제금액',
    net_pay_amount DECIMAL(15,2) DEFAULT 0 COMMENT '차인지급액',
    is_payslip_sent TINYINT(1) DEFAULT 0 COMMENT '명세서 전송 여부',
    payslip_sent_datetime DATETIME DEFAULT NULL COMMENT '명세서 전송 일시',
    formula_version_id BIGINT DEFAULT NULL COMMENT '사용된 계산식 버전 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT DEFAULT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (salary_target_id),
    UNIQUE KEY uk_salary_target (store_user_id, accrual_start_date, accrual_end_date),
    KEY idx_salary_target_store_user (store_user_id),
    KEY idx_salary_target_accrual_period (accrual_start_date, accrual_end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='급여 계산 결과';

-- 급여대상 지급항목 상세
CREATE TABLE IF NOT EXISTS payroll_detail (
    payroll_detail_id BIGINT NOT NULL COMMENT '지급항목 상세 ID',
    salary_target_id BIGINT DEFAULT NULL COMMENT '급여 대상 ID (FK)',
    store_user_id BIGINT NOT NULL COMMENT '매장-유저 ID',
    accrual_start_date DATE NOT NULL COMMENT '귀속 시작일',
    accrual_end_date DATE NOT NULL COMMENT '귀속 종료일',
    salary_item_code_id BIGINT NOT NULL COMMENT '지급 항목 코드 (기초코드)',
    amount DECIMAL(15,2) DEFAULT 0 COMMENT '지급 금액',
    base_time DECIMAL(10,2) DEFAULT NULL COMMENT '기준 시간',
    pay_rate DECIMAL(10,4) DEFAULT 1.0 COMMENT '지급율',
    formula VARCHAR(500) DEFAULT NULL COMMENT '계산방법',
    formula_text VARCHAR(500) DEFAULT NULL COMMENT '계산 방법 텍스트',
    remarks VARCHAR(200) DEFAULT NULL COMMENT '비고',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT DEFAULT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (payroll_detail_id),
    UNIQUE KEY uk_payroll_detail (store_user_id, accrual_start_date, accrual_end_date, salary_item_code_id),
    KEY idx_payroll_detail_salary_target (salary_target_id),
    CONSTRAINT fk_payroll_detail_salary_target FOREIGN KEY (salary_target_id) REFERENCES salary_target (salary_target_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='급여대상 지급항목 상세';

-- 급여대상 공제항목 상세
CREATE TABLE IF NOT EXISTS deduction_detail (
    deduction_detail_id BIGINT NOT NULL COMMENT '공제항목 상세 ID',
    salary_target_id BIGINT DEFAULT NULL COMMENT '급여 대상 ID (FK)',
    store_user_id BIGINT NOT NULL COMMENT '매장-유저 ID',
    accrual_start_date DATE NOT NULL COMMENT '귀속 시작일',
    accrual_end_date DATE NOT NULL COMMENT '귀속 종료일',
    deduction_item_code_id VARCHAR(20) NOT NULL COMMENT '공제 항목 코드 (기초코드)',
    amount DECIMAL(15,2) DEFAULT 0 COMMENT '공제 금액',
    deduction_rate DECIMAL(10,4) DEFAULT NULL COMMENT '공제율',
    formula VARCHAR(500) DEFAULT NULL COMMENT '계산방법',
    formula_text VARCHAR(500) DEFAULT NULL COMMENT '계산 방법 텍스트',
    remarks VARCHAR(200) DEFAULT NULL COMMENT '비고',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT DEFAULT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (deduction_detail_id),
    UNIQUE KEY uk_deduction_detail (store_user_id, accrual_start_date, accrual_end_date, deduction_item_code_id),
    KEY idx_deduction_detail_salary_target (salary_target_id),
    CONSTRAINT fk_deduction_detail_salary_target FOREIGN KEY (salary_target_id) REFERENCES salary_target (salary_target_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='급여대상 공제항목 상세';
