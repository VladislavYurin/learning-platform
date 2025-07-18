create table if not exists users
(
    id_user     bigint primary key,
    username    varchar(255) not null,
    email       varchar(255) not null,
    tg_nickname varchar(255) not null,
    password    varchar(255) not null,
    user_role   varchar(255) not null default 'USER'
);

CREATE TABLE IF NOT EXISTS courses
(
    id_course        bigint primary key,
    course_name      varchar(255) not null,
    description      text,
    is_active        boolean      not null default true,
    course_author_id bigint       not null,
    created_at       timestamp    not null default current_timestamp,
    foreign key (course_author_id) references users (id_user) on delete cascade
);

CREATE TABLE IF NOT EXISTS modules
(
    id_module     bigint primary key,
    module_name   varchar(255) not null,
    module_number int          not null unique,
    description   text,
    id_course     bigint       not null,
    is_active     boolean      not null default true,
    created_at    timestamp    not null default current_timestamp,
    foreign key (id_course) references courses (id_course) on delete cascade
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

CREATE TABLE IF NOT EXISTS user_course_access
(
    id_access         bigserial primary key,
    user_id           bigint    not null,
    course_id         bigint    not null,
    access_granted_at timestamp not null default current_timestamp,
    access_granted_by bigint    not null,
    foreign key (user_id) references users (id_user) on delete cascade,
    foreign key (course_id) references courses (id_course) on delete cascade,
    foreign key (access_granted_by) references users (id_user) on delete cascade,
    unique (user_id, course_id)
);

CREATE TABLE IF NOT EXISTS user_module_access
(
    id_access         bigserial primary key,
    user_id           bigint    not null,
    course_id         bigint    not null,
    module_id         bigint    not null,
    access_granted_at timestamp not null default current_timestamp,
    access_granted_by bigint    not null,
    foreign key (user_id) references users (id_user) on delete cascade,
    foreign key (course_id) references courses (id_course) on delete cascade,
    foreign key (module_id) references modules (id_module) on delete cascade,
    foreign key (access_granted_by) references users (id_user) on delete cascade,
    unique (user_id, module_id, course_id)
);