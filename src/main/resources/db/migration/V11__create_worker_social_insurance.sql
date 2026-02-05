-- 노동자 사회보험 가입 정보
CREATE TABLE IF NOT EXISTS worker_social_insurance (
    worker_social_insurance_id BIGINT NOT NULL COMMENT '사회보험 가입 ID',
    store_user_id BIGINT NOT NULL COMMENT '매장-유저 ID',
    insurance_type VARCHAR(50) NOT NULL COMMENT '보험 종류 (NATIONAL_PENSION, HEALTH_INSURANCE, EMPLOYMENT_INSURANCE, INDUSTRIAL_ACCIDENT_INSURANCE)',
    enrolled TINYINT(1) DEFAULT 0 COMMENT '가입 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT DEFAULT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (worker_social_insurance_id),
    UNIQUE KEY uk_store_user_insurance_type (store_user_id, insurance_type),
    KEY idx_worker_social_insurance_store_user (store_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='노동자 사회보험 가입 정보';
