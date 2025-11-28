-- Quartz Scheduler Tables
create table QRTZ_JOB_DETAILS
(
    SCHED_NAME        varchar(120) not null,
    JOB_NAME          varchar(200) not null,
    JOB_GROUP         varchar(200) not null,
    DESCRIPTION       varchar(250) null,
    JOB_CLASS_NAME    varchar(250) not null,
    IS_DURABLE        varchar(1)   not null,
    IS_NONCONCURRENT  varchar(1)   not null,
    IS_UPDATE_DATA    varchar(1)   not null,
    REQUESTS_RECOVERY varchar(1)   not null,
    JOB_DATA          blob         null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
) engine=InnoDB;

create table QRTZ_TRIGGERS
(
    SCHED_NAME     varchar(120) not null,
    TRIGGER_NAME   varchar(200) not null,
    TRIGGER_GROUP  varchar(200) not null,
    JOB_NAME       varchar(200) not null,
    JOB_GROUP      varchar(200) not null,
    DESCRIPTION    varchar(250) null,
    NEXT_FIRE_TIME bigint(13)   null,
    PREV_FIRE_TIME bigint(13)   null,
    PRIORITY       integer      null,
    TRIGGER_STATE  varchar(16)  not null,
    TRIGGER_TYPE   varchar(8)   not null,
    START_TIME     bigint(13)   not null,
    END_TIME       bigint(13)   null,
    CALENDAR_NAME  varchar(200) null,
    MISFIRE_INSTR  smallint(2)  null,
    JOB_DATA       blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_TRIGGERS_FK1 foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP)
        references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
) engine=InnoDB;

create table QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(200) not null,
    TRIGGER_GROUP   varchar(200) not null,
    REPEAT_COUNT    bigint(7)    not null,
    REPEAT_INTERVAL bigint(12)   not null,
    TIMES_TRIGGERED bigint(10)   not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPLE_TRIG_FK foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
) engine=InnoDB;

create table QRTZ_CRON_TRIGGERS
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(200) not null,
    TRIGGER_GROUP   varchar(200) not null,
    CRON_EXPRESSION varchar(120) not null,
    TIME_ZONE_ID    varchar(80)  null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_CRON_TRIG_FK foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
) engine=InnoDB;

create table QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_NAME  varchar(200) not null,
    TRIGGER_GROUP varchar(200) not null,
    STR_PROP_1    varchar(512) null,
    STR_PROP_2    varchar(512) null,
    STR_PROP_3    varchar(512) null,
    INT_PROP_1    int          null,
    INT_PROP_2    int          null,
    LONG_PROP_1   bigint       null,
    LONG_PROP_2   bigint       null,
    DEC_PROP_1    decimal(13,4) null,
    DEC_PROP_2    decimal(13,4) null,
    BOOL_PROP_1   varchar(1)   null,
    BOOL_PROP_2   varchar(1)   null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPROP_TRIG_FK foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
) engine=InnoDB;

create table QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_NAME  varchar(200) not null,
    TRIGGER_GROUP varchar(200) not null,
    BLOB_DATA     blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_BLOB_TRIG_FK foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
) engine=InnoDB;

create table QRTZ_CALENDARS
(
    SCHED_NAME    varchar(120) not null,
    CALENDAR_NAME varchar(200) not null,
    CALENDAR      blob         not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
) engine=InnoDB;

create table QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_GROUP varchar(200) not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
) engine=InnoDB;

create table QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME        varchar(120) not null,
    ENTRY_ID          varchar(140) not null,
    TRIGGER_NAME      varchar(200) not null,
    TRIGGER_GROUP     varchar(200) not null,
    INSTANCE_NAME     varchar(200) not null,
    FIRED_TIME        bigint(13)   not null,
    SCHED_TIME        bigint(13)   not null,
    PRIORITY          integer      not null,
    STATE             varchar(16)  not null,
    JOB_NAME          varchar(200) null,
    JOB_GROUP         varchar(200) null,
    IS_NONCONCURRENT  varchar(1)   null,
    REQUESTS_RECOVERY varchar(1)   null,
    primary key (SCHED_NAME, ENTRY_ID)
) engine=InnoDB;

create table QRTZ_SCHEDULER_STATE
(
    SCHED_NAME        varchar(120) not null,
    INSTANCE_NAME     varchar(200) not null,
    LAST_CHECKIN_TIME bigint(13)   not null,
    CHECKIN_INTERVAL  bigint(13)   not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
) engine=InnoDB;

create table QRTZ_LOCKS
(
    SCHED_NAME varchar(120) not null,
    LOCK_NAME  varchar(40)  not null,
    primary key (SCHED_NAME, LOCK_NAME)
) engine=InnoDB;

create index IDX_QRTZ_J_REQ_RECOVERY on QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);
create index IDX_QRTZ_J_GRP on QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_T_J on QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
create index IDX_QRTZ_T_JG on QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);
create index IDX_QRTZ_T_C on QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);
create index IDX_QRTZ_T_STATE on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);
create index IDX_QRTZ_T_N_STATE on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
create index IDX_QRTZ_T_N_G_STATE on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
create index IDX_QRTZ_T_NEXT_FIRE_TIME on QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);
create index IDX_QRTZ_T_NFT_ST on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
create index IDX_QRTZ_T_NFT_MISFIRE on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
create index IDX_QRTZ_T_NFT_ST_MISFIRE on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
create index IDX_QRTZ_T_NFT_ST_MISFIRE_GRP on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_FT_TRIG_INST_NAME on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);
create index IDX_QRTZ_FT_INST_JOB_REQ_RCVRY on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
create index IDX_QRTZ_FT_J_G on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
create index IDX_QRTZ_FT_JG on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);
create index IDX_QRTZ_FT_T_G on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
create index IDX_QRTZ_FT_TG on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

-- Store/User Tables
CREATE TABLE IF NOT EXISTS store_info (
    store_id BIGINT NOT null PRIMARY KEY COMMENT '매장 고유 ID',
    store_name VARCHAR(100) NOT NULL COMMENT '매장명',
    business_no VARCHAR(20) NOT NULL COMMENT '사업자등록번호',
    owner_name VARCHAR(20) NOT NULL COMMENT '대표자명',
    industry VARCHAR(50) NOT NULL COMMENT '업종',
    owner_phone VARCHAR(20) NOT NULL COMMENT '대표 전화번호',
    store_zip_code VARCHAR(5) NOT NULL COMMENT '우편번호',
    detailed_address VARCHAR(200) NOT NULL COMMENT '상세주소'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회사 등록 정보 테이블';

CREATE TABLE IF NOT EXISTS user_info (
    user_id BIGINT NOT NULL PRIMARY KEY COMMENT '유저 고유 ID',
    login_id VARCHAR(50) NOT NULL COMMENT '로그인 아이디',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호',
    email VARCHAR(100) NOT NULL COMMENT '이메일',
    is_terms_agreed TINYINT NOT NULL COMMENT '이용약관 동의 여부',
    is_privacy_agreed TINYINT NOT NULL COMMENT '개인정보처리방침 동의 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    UNIQUE (login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자 정보 테이블';

CREATE TABLE IF NOT EXISTS store_user (
    store_user_id BIGINT not null COMMENT '매장-유저 관계 아이디',
    store_id BIGINT NOT NULL COMMENT '매장 아이디',
    user_id BIGINT NOT NULL COMMENT '계정아이디',
    user_type CHAR(1) NOT NULL COMMENT '사용자 구분 (A. 관리자, E. 직원)',
    emp_code VARCHAR(20) NOT NULL COMMENT '사원코드',
    emp_name VARCHAR(100) NOT NULL COMMENT '사원명',
    resident_no VARCHAR(13) NOT NULL COMMENT '주민등록번호',
    phone VARCHAR(20) NOT NULL COMMENT '연락처',
    position_code_id bigint NOT NULL COMMENT '직책아이디(기초코드)',
    job_type_code_id bigint NOT NULL COMMENT '고용형태(기초코드)',
    work_type_code_id bigint not null comment '근무유형(기초코드)',
    hire_date date NOT NULL COMMENT '입사일',
    end_date date DEFAULT NULL COMMENT '퇴사일',
    contract_file varchar(32) DEFAULT NULL COMMENT '근로계약서파일(파일아이디)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    primary key (store_user_id),
    unique (store_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='매장-유저 관계 테이블';

CREATE TABLE IF NOT EXISTS code_type (
    code_type VARCHAR(20) NOT NULL COMMENT '구분 (직책 : POSITION, 고용형태 : JOB, 근무유형 : WORK_TYPE, 급여형태 : PAY_TYPE, 지급항목 : PAY_ITEM, 공제항목 : DEDUCT_ITEM, 계산식항목 : FORMULA_ITEM)',
    code_type_name VARCHAR(128) NOT NULL COMMENT '구분명',
    PRIMARY KEY (code_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='기초코드 타입';

CREATE TABLE IF NOT EXISTS store_code (
    store_code_id BIGINT not null,
    store_id BIGINT NOT NULL COMMENT '매장 아이디',
    code_type VARCHAR(20) NOT NULL COMMENT '구분 (예: PAY_ITEM)',
    code VARCHAR(20) NOT NULL COMMENT '내역코드',
    code_name VARCHAR(128) NOT NULL COMMENT '내역명',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    primary key(store_code_id),
    unique (store_id, code_type, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회사별 기초코드 정의';

CREATE TABLE IF NOT EXISTS system_config (
    store_id BIGINT NOT NULL COMMENT '매장 아이디',
    config_key VARCHAR(50) NOT NULL COMMENT '설정 키',
    config_value VARCHAR(100) DEFAULT NULL COMMENT '설정 값',
    description VARCHAR(255) DEFAULT NULL COMMENT '설명',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by BIGINT DEFAULT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by BIGINT DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (store_id, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회사 환경설정';

-- Attend Tables

CREATE TABLE IF NOT EXISTS work_time_config (
    work_time_id bigint not null,
    store_id bigint NOT NULL COMMENT '매장 아이디',
    worker_type CHAR(1) NOT NULL COMMENT '알바/직원구분 (P: 알바, E: 직원)',
    work_time_code VARCHAR(20) NOT NULL COMMENT '근무시간코드',
    work_time_name VARCHAR(128) NOT NULL COMMENT '근무시간명',
    start_time time NOT NULL COMMENT '근무시작시간 (예: 0900)',
    end_time time NOT NULL COMMENT '근무종료시간 (예: 1200)',
    break_start_time time NOT NULL COMMENT '휴게시작시간 (예: 0900)',
    break_end_time time NOT NULL COMMENT '휴게종료시간 (예: 1200)',
    is_deleted tinyint NOT NULL COMMENT '삭제여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    primary key(work_time_id),
    unique (store_id, worker_type, work_time_code) -- 회사코드 + 알바/직원구분 + 근무시간코드로 유니크 설정
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='근무시간 설정 테이블';

CREATE TABLE IF NOT EXISTS employee_attend_info (
    store_user_id bigint NOT NULL,
    work_days CHAR(3) NOT NULL COMMENT '근무요일 (예: 001 (월요일), 002 (화요일), ...)',
    work_time_id bigint NOT NULL COMMENT '근무시간설정 아이디',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (store_user_id, work_days, work_time_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사원 근무(근태) 정보 테이블';



CREATE TABLE IF NOT EXISTS employee_work_schedule (
    work_schedule_id bigint not null,
    store_user_id bigint NOT NULL COMMENT '회사-유저 아이디',
    schedule_date DATE NOT NULL COMMENT '스케줄일자 (YYYY-MM-DD)',
    work_time_id bigint DEFAULT NULL COMMENT '근무시간아이디 (NULL일 경우 사용자 입력)',
    planned_start TIME DEFAULT NULL COMMENT '근무시작시간 (hh:mm)',
    planned_end TIME DEFAULT NULL COMMENT '근무종료시간 (hh:mm)',
    source VARCHAR(20) NOT NULL COMMENT '입력방식 (AUTO: 자동불러오기, USER: 사용자직접입력)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    primary key (work_schedule_id),
    unique (store_user_id, schedule_date) -- 한 사원이 하루에 하나의 근무스케줄을 가진다는 전제. 비즈니스 규칙 변경시 제약조건 해제 필요
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='근무스케줄 관리 테이블';

CREATE TABLE IF NOT EXISTS attendance_record (
    attendance_id BIGINT NOT NULL COMMENT '출퇴근등록 고유ID',
    store_user_id bigint NOT NULL COMMENT '매장-사용자 아이디',
    work_date DATE NOT NULL COMMENT '근무일자 (YYYY-MM-DD)',
    punch_type VARCHAR(20) NOT NULL COMMENT '출퇴근구분 (IN, OUT, BREAK_START, BREAK_END)',
    punch_time DATETIME NOT NULL COMMENT '등록시간',
    source VARCHAR(4) DEFAULT NULL COMMENT '등록방식 (QR / GPS)',
    location_info VARCHAR(255) DEFAULT NULL COMMENT 'GPS정보',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (attendance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='출퇴근 등록 테이블';

CREATE TABLE IF NOT EXISTS attendance_result (
    store_user_id bigint NOT NULL COMMENT '매장-사용자 아이디',
    ym CHAR(6) NOT NULL COMMENT '귀속월(YYYYMM)',
    work_date date NOT NULL COMMENT '근무일자(YYYYMMDD)',
    actual_in TIME DEFAULT NULL COMMENT '실제 출근시간',
    actual_out TIME DEFAULT NULL COMMENT '실제 퇴근시간',
    recognized_in TIME DEFAULT NULL COMMENT '인정 출근시간',
    recognized_out TIME DEFAULT NULL COMMENT '인정 퇴근시간',
    work_minutes INT DEFAULT NULL COMMENT '실제 근무시간(분)',
    overtime_min INT DEFAULT NULL COMMENT '연장근로시간(분)',
    recognized_min INT DEFAULT NULL COMMENT '인정 근무시간(분)',
    attendance_status CHAR(2) DEFAULT NULL COMMENT '근태결과 (00정상,01지각,02결근)',
    is_overtime_work tinyint DEFAULT NULL COMMENT '연장근무 여부(1/0)',
    is_holiday_work tinyint DEFAULT NULL COMMENT '휴일근무 여부(1/0)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (store_user_id, ym, work_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='출퇴근 상세 결과';



CREATE TABLE IF NOT EXISTS attendance_monthly_aggregation (
    store_user_id bigint NOT NULL COMMENT '매장-사용자 아이디',
    ym VARCHAR(6) NOT NULL COMMENT '귀속월(YYYYMM)',
    total_work_min INT DEFAULT 0 COMMENT '월 총 실제 근무시간(분)',
    total_recognized_min INT DEFAULT 0 COMMENT '월 총 인정 근무시간(분)',
    total_overtime_min INT DEFAULT 0 COMMENT '월 총 연장 근로시간(분)',
    late_days INT DEFAULT 0 COMMENT '월 지각 일수',
    early_leave_days INT DEFAULT 0 COMMENT '월 조퇴 일수',
    absent_days INT DEFAULT 0 COMMENT '월 결근 일수',
    normal_work_days INT DEFAULT 0 COMMENT '월 정상근무 일수',
    overtime_days INT DEFAULT 0 COMMENT '월 연장 일수',
    holiday_work_min INT DEFAULT 0 COMMENT '월 휴일 근무시간(분)',
    night_work_min INT DEFAULT 0 COMMENT '월 야간 근무시간(분)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
    created_by bigint NOT NULL COMMENT '생성자',
    modify_at DATETIME DEFAULT NULL COMMENT '수정시점',
    modify_by bigint DEFAULT NULL COMMENT '수정자',
    PRIMARY KEY (store_user_id, ym)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='출퇴근 월별 집계';