CREATE TABLE IF NOT EXISTS store_invite (
    store_invite_id BIGINT NOT NULL COMMENT '매장 초대 고유 ID',
    store_id BIGINT NOT NULL COMMENT '매장 아이디',
    employee_name VARCHAR(100) NOT NULL COMMENT '초대 대상 이름',
    employee_phone VARCHAR(20) NOT NULL COMMENT '초대 대상 연락처',
    resident_no VARCHAR(13) NOT NULL COMMENT '초대 대상 주민등록번호',
    invite_token VARCHAR(64) NOT NULL COMMENT '초대 토큰',
    invite_status VARCHAR(20) NOT NULL COMMENT '초대 상태 (PENDING, ACCEPTED, EXPIRED)',
    expires_at DATETIME NOT NULL COMMENT '토큰 만료시점',
    accepted_at DATETIME DEFAULT NULL COMMENT '승인시점',
    accepted_user_id BIGINT DEFAULT NULL COMMENT '승인한 사용자 ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (store_invite_id),
    UNIQUE KEY idx_store_invite_token (invite_token),
    KEY idx_store_invite_store_status (store_id, invite_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='매장 초대 링크 관리 테이블';
