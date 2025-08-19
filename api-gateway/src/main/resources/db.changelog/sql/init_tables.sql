create table if not exists users
(
    id_user          BIGSERIAL primary key,
    username         varchar(255) not null,
    firstname        varchar(255) not null,
    lastname         varchar(255) not null,
    tg_nickname      varchar(255) not null,
    password         varchar(255) not null,
    telegram_chat_id bigint,
    user_role        varchar(255) not null default 'USER'
);

CREATE TABLE IF NOT EXISTS courses
(
    id_course        BIGSERIAL primary key,
    course_title     varchar(255) not null,
    description      text,
    is_active        boolean      not null default true,
    course_author_id bigint       not null,
    created_at       timestamp    not null default current_timestamp,
    foreign key (course_author_id) references users (id_user) on delete cascade
);

CREATE TABLE IF NOT EXISTS modules
(
    id_module      BIGSERIAL primary key,
    module_title   varchar(255) not null,
    module_number  int          not null,
    module_content text,
    id_course      bigint       not null,
    is_active      boolean      not null default true,
    created_at     timestamp    not null default current_timestamp,
    foreign key (id_course) references courses (id_course) on delete cascade
);

CREATE TABLE IF NOT EXISTS user_course_access
(
    id_access         BIGSERIAL primary key,
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
    id_access         BIGSERIAL primary key,
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

CREATE TABLE IF NOT EXISTS mentor_time_slots
(
    id_slot          BIGSERIAL PRIMARY KEY,
    mentor_id        BIGINT      NOT NULL,
    start_time       TIMESTAMP   NOT NULL,
    end_time         TIMESTAMP   NOT NULL,
    slot_type        VARCHAR(50) NOT NULL CHECK (slot_type IN ('INDIVIDUAL', 'GROUP')),
    slot_status      VARCHAR(50) NOT NULL CHECK (slot_status IN
                                                 ('ACQUAINTANCE', 'COMMUNICATION', 'ACCEPTING')),
    max_participants INT         NOT NULL DEFAULT 1,
    is_active        BOOLEAN     NOT NULL DEFAULT TRUE,
    meeting_link     VARCHAR(255),
    description      TEXT,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentor_id) REFERENCES users (id_user) ON DELETE CASCADE,
    CONSTRAINT valid_slot_time CHECK (end_time > start_time),
    CONSTRAINT individual_slot_limit CHECK (
        (slot_type = 'INDIVIDUAL' AND max_participants = 1) OR
        (slot_type = 'GROUP' AND max_participants >= 1)
        )
);

CREATE TABLE IF NOT EXISTS booked_time_slots
(
    id_booking      BIGSERIAL PRIMARY KEY,
    slot_id         BIGINT    NOT NULL,
    mentor_notes    TEXT,
    meeting_outcome TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (slot_id) REFERENCES mentor_time_slots (id_slot) ON DELETE CASCADE,
    CONSTRAINT unique_slot_booking UNIQUE (slot_id)
);

CREATE TABLE IF NOT EXISTS call_participants
(
    id_participation BIGSERIAL PRIMARY KEY,
    booking_id       BIGINT    NOT NULL,
    mentee_id        BIGINT    NOT NULL,
    attended         BOOLEAN            DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES booked_time_slots (id_booking) ON DELETE CASCADE,
    FOREIGN KEY (mentee_id) REFERENCES users (id_user) ON DELETE CASCADE,
    CONSTRAINT unique_mentee_booking UNIQUE (booking_id, mentee_id)
);