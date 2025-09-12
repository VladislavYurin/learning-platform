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
    id_slot           BIGSERIAL PRIMARY KEY,
    mentor_id         BIGINT      NOT NULL,
    start_time        TIMESTAMP   NOT NULL,
    end_time          TIMESTAMP   NOT NULL,
    slot_type         VARCHAR(50) NOT NULL CHECK (slot_type IN ('INDIVIDUAL', 'GROUP')),
    slot_meeting_type VARCHAR(50) NOT NULL CHECK
        (slot_meeting_type IN ('ACQUAINTANCE', 'COMMUNICATION', 'ACCEPTING')),
    max_participants  INTEGER    NOT NULL DEFAULT 1,
    meeting_link      TEXT,
    description       TEXT,
    is_active         boolean     not null default true,
    created_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mentor_id) REFERENCES users (id_user) ON DELETE CASCADE,
    CONSTRAINT valid_slot_time CHECK (end_time > start_time),
    CONSTRAINT individual_slot_limit CHECK (
        (slot_type = 'INDIVIDUAL' AND max_participants = 1) OR
        (slot_type = 'GROUP' AND max_participants >= 1)
        )
);

CREATE TABLE IF NOT EXISTS mentor_time_slot__users
(
    id           BIGSERIAL PRIMARY KEY,
    time_slot_id BIGINT NOT NULL,
    user_id      BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS notification_templates
(
    id_template         BIGSERIAL       PRIMARY KEY,
    template_type           VARCHAR(255)    NOT NULL,
    template_text           TEXT            NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sent_notification
(
    notification_id          BIGSERIAL          PRIMARY KEY,
    notification_type        VARCHAR(255)       NOT NULL ,
    recipient_id             BIGSERIAL          NOT NULL,
    notification_status      VARCHAR(255)       NOT NULL,
    notification_destination VARCHAR(255),
    error_text               VARCHAR(255),
    CONSTRAINT fk_recipient FOREIGN KEY (recipient_id)
    REFERENCES users (id_user)
);

CREATE TABLE IF NOT EXISTS booked_time_slots
(
    id_slot             BIGSERIAL     PRIMARY KEY,
    mentor_id           BIGINT        NOT NULL,
    mentee_id           BIGINT        NOT NULL,
    start_time          TIMESTAMP     NOT NULL,
    end_time            TIMESTAMP     NOT NULL,
    slot_id             BIGINT        NOT NULL,
    booking_status_type VARCHAR(50)   NOT NULL,
    FOREIGN KEY (mentor_id) REFERENCES users (id_user) ON DELETE RESTRICT,
    FOREIGN KEY (mentee_id) REFERENCES users (id_user) ON DELETE RESTRICT,
    FOREIGN KEY (slot_id) REFERENCES mentor_time_slots(id_slot) ON DELETE RESTRICT,
    CONSTRAINT valid_slot_time CHECK (end_time > start_time),
    CONSTRAINT booking_status_type CHECK (
        (booking_status_type IN ('REQUESTED', 'CONFIRMED'))
        )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_active_booking_per_slot_mentee
ON booked_time_slots (slot_id, mentee_id)
WHERE booking_status_type IN ('REQUESTED','CONFIRMED');

-- Вставка шаблонов уведомлений
INSERT INTO notification_templates(template_type, template_text)
VALUES
('COURSE_ACCESS_GRANTED', $$
Уважаемый %s!
Вам предоставлен доступ к курсу "%s".
Доступ предоставил: %s %s
Дата предоставления: %s$$),

('MODULE_ACCESS_GRANTED', $$
Уважаемый %s!
Открыт новый модуль "%s" в курсе "%s".
Доступ предоставил: %s %s
Дата предоставления: %s$$),

('COURSE_CREATED_MENTOR', $$
Уважаемый, %s!
Создан новый курс "%s".
Автор курса: %s %s.
Получатель: %s %s.
Дата создания: %s.$$),

('MODULE_CREATED_MENTOR', $$
Уважаемый, %s!
Создан новый модуль "%s" в курсе "%s".
Автор модуля: %s %s.
Получатель: %s %s.
Дата создания: %s.$$),

('COURSE_DELETED', $$
Уважаемый, %s!
Курс "%s" удален.$$),

('USER_REGISTRATION_USER', $$
Уважаемый, %s!
Вы успешно зарегистрированы!
Дата создания: %s.$$),

('COURSE_ACCESS_REVOKED', $$
Уважаемый, %s!
Доступ к курсу "%s" отозван.
Дата создания: %s.$$),

('MODULE_ACCESS_REVOKED', $$
Уважаемый, %s!
Доступ к модулю "%s" отозван.
Дата создания: %s.$$),

('SLOT_BOOKED_MENTOR', $$
Уважаемый, %s!
Слот на дату и время: %s - %s
забронирован пользователем %s %s.$$);