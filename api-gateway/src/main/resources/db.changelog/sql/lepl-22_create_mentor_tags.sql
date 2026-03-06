CREATE TABLE IF NOT EXISTS mentor_tags
(
    id_tag          BIGSERIAL PRIMARY KEY,
    mentor_tag_name VARCHAR(255) NOT NULL UNIQUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active       boolean      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS mentor_tag_link
(
    id_mentor_tag BIGSERIAL PRIMARY KEY,
    id_mentor     BIGINT    NOT NULL,
    id_tag        BIGINT    NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mentor_tags_mentor
        FOREIGN KEY (id_mentor) REFERENCES users (id_user) ON DELETE CASCADE,
    CONSTRAINT fk_mentor_tags_tag
        FOREIGN KEY (id_tag) REFERENCES mentor_tags (id_tag) ON DELETE CASCADE,

    CONSTRAINT uk_mentor_tags_mentor_tag UNIQUE (id_mentor, id_tag)
);

INSERT INTO mentor_tags (mentor_tag_name)
VALUES ('backend'),
       ('frontend'),
       ('java'),
       ('react'),
       ('system analysis'),
       ('devops'),
       ('mobile'),
       ('testing'),
       ('machine learning'),
       ('data science'),
       ('python'),
       ('c++'),
       ('go'),
       ('kotlin'),
       ('swift'),
       ('javascript'),
       ('angular'),
       ('vue'),
       ('django'),
       ('node.js'),
       ('Лучший учитель года 1998'),
       ('Заслуженный учитель с. Большие Залупки')
ON CONFLICT (mentor_tag_name) DO NOTHING;