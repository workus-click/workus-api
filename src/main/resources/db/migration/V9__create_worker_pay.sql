-- 노동자 급여 정보 테이블
-- SINGLE_TABLE 상속 전략 사용 (pay_type으로 구분)
CREATE TABLE IF NOT EXISTS worker_pay (
    worker_pay_id BIGINT PRIMARY KEY,
    store_user_id BIGINT NOT NULL COMMENT '매장 사용자 ID',
    pay_type VARCHAR(20) NOT NULL COMMENT '급여 형태 (HOURLY, MONTHLY)',
    work_hours_per_day INT NULL COMMENT '일 근무시간',
    work_days_per_week INT NULL COMMENT '주 근무일수',
    dependents_cnt INT NULL COMMENT '부양가족 수',
    nationality VARCHAR(10) NOT NULL DEFAULT 'KR' COMMENT '국적 (ISO 3166-1 alpha-2)',
    hourly_rate DECIMAL(15,2) NULL COMMENT '시급 (시급제인 경우)',
    monthly_salary DECIMAL(15,2) NULL COMMENT '월급 (월급제인 경우)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_worker_pay_store_user (store_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='노동자 급여 정보';
