-- 간이세액조견표 테이블
-- 조회 전용 참조 데이터로, 연 1회 국세청 간이세액표 개정 시 SQL로 직접 입력
-- AUTO_INCREMENT 사용: 사용자가 유연하게 추가하는 데이터가 아니므로 Flyway SQL로 관리
CREATE TABLE IF NOT EXISTS income_tax_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year CHAR(4) NOT NULL COMMENT '기준연도',
    salary_from INT NOT NULL COMMENT '급여 시작구간 (천원)',
    salary_to INT NOT NULL COMMENT '급여 종료구간 (천원)',
    total_dependents_cnt INT NOT NULL COMMENT '부양가족수',
    tax_amount DECIMAL(15,2) NOT NULL COMMENT '산출세액 (원)',
    UNIQUE KEY uk_income_tax_table (year, salary_from, salary_to, total_dependents_cnt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='간이세액조견표';
