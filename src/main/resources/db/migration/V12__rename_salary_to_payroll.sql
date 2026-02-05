-- =====================================================
-- V12: Salary 관련 테이블명을 Payroll로 통일
-- =====================================================

-- 1. salary_calculation_formula -> payroll_formula 변경
RENAME TABLE salary_calculation_formula TO payroll_formula;

-- 2. salary_calculation_formula_version -> payroll_formula_version 변경
RENAME TABLE salary_calculation_formula_version TO payroll_formula_version;

-- 3. salary_target -> payroll 변경
-- 먼저 외래키 제약조건 삭제
ALTER TABLE payroll_detail DROP FOREIGN KEY fk_payroll_detail_salary_target;
ALTER TABLE deduction_detail DROP FOREIGN KEY fk_deduction_detail_salary_target;

-- 테이블 이름 변경
RENAME TABLE salary_target TO payroll;

-- salary_target_id 컬럼을 payroll_id로 변경
ALTER TABLE payroll CHANGE COLUMN salary_target_id payroll_id BIGINT NOT NULL COMMENT '급여 ID';

-- 인덱스 및 유니크키 이름 변경
ALTER TABLE payroll DROP INDEX uk_salary_target;
ALTER TABLE payroll ADD UNIQUE KEY uk_payroll (store_user_id, accrual_start_date, accrual_end_date);

ALTER TABLE payroll DROP INDEX idx_salary_target_store_user;
ALTER TABLE payroll ADD INDEX idx_payroll_store_user (store_user_id);

ALTER TABLE payroll DROP INDEX idx_salary_target_accrual_period;
ALTER TABLE payroll ADD INDEX idx_payroll_accrual_period (accrual_start_date, accrual_end_date);

-- payroll_detail의 salary_target_id를 payroll_id로 변경
ALTER TABLE payroll_detail CHANGE COLUMN salary_target_id payroll_id BIGINT DEFAULT NULL COMMENT '급여 ID (FK)';
ALTER TABLE payroll_detail DROP INDEX idx_payroll_detail_salary_target;
ALTER TABLE payroll_detail ADD INDEX idx_payroll_detail_payroll (payroll_id);
ALTER TABLE payroll_detail ADD CONSTRAINT fk_payroll_detail_payroll FOREIGN KEY (payroll_id) REFERENCES payroll (payroll_id) ON DELETE CASCADE;

-- deduction_detail의 salary_target_id를 payroll_id로 변경
ALTER TABLE deduction_detail CHANGE COLUMN salary_target_id payroll_id BIGINT DEFAULT NULL COMMENT '급여 ID (FK)';
ALTER TABLE deduction_detail DROP INDEX idx_deduction_detail_salary_target;
ALTER TABLE deduction_detail ADD INDEX idx_deduction_detail_payroll (payroll_id);
ALTER TABLE deduction_detail ADD CONSTRAINT fk_deduction_detail_payroll FOREIGN KEY (payroll_id) REFERENCES payroll (payroll_id) ON DELETE CASCADE;
