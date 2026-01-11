alter table employee_work_schedule
    change source creation_type VARCHAR(20) NOT NULL COMMENT '입력방식 (AUTO: 자동불러오기, USER: 사용자직접입력)';
