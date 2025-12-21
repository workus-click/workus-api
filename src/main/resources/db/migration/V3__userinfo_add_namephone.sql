alter table user_info
    add name VARCHAR(100) null comment '이름' after password;

alter table user_info
    add phone VARCHAR(20) null comment '연락처'after password;

