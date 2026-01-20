CREATE TABLE IF NOT EXISTS revinfo (
    rev bigint not null auto_increment,
    revtstmp bigint not null,
    actor_type varchar(16) not null,
    actor_id bigint not null,
    primary key (REV)
);