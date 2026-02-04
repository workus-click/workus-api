-- 2025년 사회보험 요율 데이터
-- PENSION: 국민연금 (7월 기준 변경)
-- HEALTH: 건강보험
-- LONGCARE: 장기요양보험 (건강보험료의 12.95%)
-- EMPLOY: 고용보험 (실업급여 부분)
INSERT INTO social_insurance_rates 
    (year, insurance_type, employee_rate, employer_rate, total_rate, effective_from, effective_to, min_base_amount, max_base_amount) 
VALUES 
    ('2025', 'PENSION', 0.0450, 0.0450, 0.0900, '2025-07-01', '2026-06-30', 400000, 6370000),
    ('2025', 'HEALTH', 0.03545, 0.03545, 0.0709, '2025-01-01', '2025-12-31', 279266, 127056982),
    ('2025', 'LONGCARE', 0.06475, 0.06475, 0.1295, '2025-01-01', '2025-12-31', NULL, NULL),
    ('2025', 'EMPLOY', 0.0090, 0.0090, 0.0180, '2025-01-01', '2025-12-31', NULL, NULL);
