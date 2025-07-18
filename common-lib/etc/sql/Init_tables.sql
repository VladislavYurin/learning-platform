create table if not exists users
(
    id_users         bigint primary key,
    username         varchar(255) not null,
    email            varchar(255) not null,
    tg_nickname      varchar(255) not null,
    password    varchar(255) not null,
    user_role        varchar(255) not null default 'ROLE_USER',
    access_id_module bigint       not null,
    foreign key (access_id_module) references modules (id_module) on delete cascade
);

CREATE TABLE IF NOT EXISTS modules
(
    id_module     bigint primary key,
    module_name   varchar(255) not null,
    module_number int          not null unique,
    description   text,
    is_active     boolean      not null default true,
    created_at    timestamp    not null default current_timestamp
);

CREATE TABLE IF NOT EXISTS questions
(
    id_question   bigint primary key,
    id_module     bigint    not null,
    question_text text      not null,
    answer_text   text      not null,
    created_at    timestamp not null default current_timestamp,
    foreign key (id_module) references modules (id_module) on delete cascade
);


