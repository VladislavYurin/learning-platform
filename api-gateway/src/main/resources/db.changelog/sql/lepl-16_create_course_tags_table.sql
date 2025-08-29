CREATE TABLE IF NOT EXISTS course_tags
(
    id_tag     BIGSERIAL PRIMARY KEY,
    tag_name   VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active  boolean      NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS course_tag_link
(
    id_course_tag BIGSERIAL PRIMARY KEY,
    id_course     BIGINT    NOT NULL,
    id_tag        BIGINT    NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_course_tags_course
    FOREIGN KEY (id_course) REFERENCES courses (id_course) ON DELETE CASCADE,
    CONSTRAINT fk_course_tags_tag
    FOREIGN KEY (id_tag) REFERENCES course_tags (id_tag) ON DELETE CASCADE,

    CONSTRAINT uk_course_tags_course_tag UNIQUE (id_course, id_tag)
    );

INSERT INTO course_tags (tag_name)
VALUES ('backend'),
       ('frontend'),
       ('java'),
       ('react'),
       ('system analysis'),
       ('database'),
       ('devops'),
       ('mobile'),
       ('testing'),
       ('security'),
       ('cloud'),
       ('machine learning'),
       ('data science'),
       ('algorithms'),
       ('python'),
       ('c++'),
       ('go'),
       ('kotlin'),
       ('swift'),
       ('php'),
       ('typescript'),
       ('javascript'),
       ('spring'),
       ('hibernate'),
       ('angular'),
       ('vue'),
       ('django'),
       ('flask'),
       ('node.js'),
       ('express'),
       ('design patterns')
    ON CONFLICT (tag_name) DO NOTHING;