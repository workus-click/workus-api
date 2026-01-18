CREATE TABLE IF NOT EXISTS `employee_work_schedule_aud` (
  `work_schedule_id` bigint(20) NOT NULL COMMENT '근무스케줄 ID',
  `rev` int NOT NULL COMMENT '리비전 ID',
  `revtype` tinyint NOT NULL COMMENT '변경유형 (0:INSERT, 1:UPDATE, 2:DELETE)',
  `store_user_id` bigint(20) COMMENT '회사-유저 아이디',
  `schedule_date` date COMMENT '스케줄일자 (YYYY-MM-DD)',
  `work_time_id` bigint(20) DEFAULT NULL COMMENT '근무시간아이디 (NULL일 경우 사용자 입력)',
  `work_start` time COMMENT '근무시작시간 (hh:mm)',
  `work_end` time COMMENT '근무종료시간 (hh:mm)',
  `break_start` time DEFAULT NULL COMMENT '휴게시작시간 (hh:mm)',
  `break_end` time DEFAULT NULL COMMENT '휴게종료시간 (hh:mm)',
  `creation_type` varchar(20) COMMENT '입력방식 (AUTO: 자동불러오기, USER: 사용자직접입력)',
  `created_at` datetime COMMENT '생성시점',
  `created_by` bigint(20) COMMENT '생성자',
  `modify_at` datetime DEFAULT NULL COMMENT '수정시점',
  `modify_by` bigint(20) DEFAULT NULL COMMENT '수정자',
  PRIMARY KEY (`work_schedule_id`, `rev`),
  KEY `idx_employee_work_schedule_aud_rev` (`rev`)
) COMMENT='근무스케줄 관리 이력 테이블';