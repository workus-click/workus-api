ALTER TABLE employee_work_schedule
CHANGE planned_start work_start TIME NOT NULL COMMENT '근무시작시간 (hh:mm)',
CHANGE planned_end work_end TIME NOT NULL COMMENT '근무종료시간 (hh:mm)';

ALTER TABLE employee_work_schedule
ADD COLUMN break_end TIME DEFAULT NULL COMMENT '휴게종료시간 (hh:mm)' AFTER work_end,
ADD COLUMN break_start TIME DEFAULT NULL COMMENT '휴게시작시간 (hh:mm)' AFTER work_end;