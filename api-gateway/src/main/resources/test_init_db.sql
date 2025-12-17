-- Это скрипт с тестовыми данными для ручного тестирования. Не надо добавлять его в ликву, накатывай вручную!
-- Пароль у первого admin в таблице: testtesttest
-- Пароль у всех остальных пользователей: 140698

TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- ВСТАВКА ДАННЫХ

INSERT INTO users (username, firstname, lastname, tg_nickname, password, telegram_chat_id,
                   user_role)
VALUES ('admin@example.com', 'Админов', 'Админ', '@tg',
        '$2a$10$MFY.aLxmbo9AFQoIDvJs6.im5YzvuyDWb30PX6yllzoGs/ICB/FLm$2a$10$MFY.aLxmbo9AFQoIDvJs6.im5YzvuyDWb30PX6yllzoGs/ICB/FLm', null, 'ADMIN'),
       ('mentor@gmail.com', 'Владислав', 'Юрин', 'vladislavyurin',
        '$2a$10$rI3efknOsFAfxj0/WDGO9e/vP0zVxV29/cL0HuKuQtKP/WGKVw6Ke', null, 'MENTOR'),
       ('pipa@popa.ru', 'Попов', 'Пипа', '@vladislavyurin',
        '$2a$10$//Y4KYfnOf8DhsIzsRMcou6jR4K2BIHnMtGam0FX0UoU5ZG2FIMo.', null, 'USER'),
       ('belotelovmaksim89@gmail.com', 'u4FirstName', 'u4LastName', '@testuser4',
        '$2a$08$Lnr95hcXqENq2Um5qjxjL.bNYGXaAPCTE50UtMzhb6/xQCcfbYLEi', 100000004, 'MENTOR'),
       ('test_user_5@cloud.com', 'u5FirstName', 'u5LastName', '@testuser5',
        '$2a$08$4ccWnP5FRiw6SkBRn4qIJeT2C2y1uHXKyxZWDwQjcNH7ECFqz0jGW', 100000005, 'MENTOR'),
       ('test_user_6@bk.ru', 'u6FirstName', 'u6LastName', '@testuser6',
        '$2a$08$23GTHm2e1Wk8LjPeYsMV0OQs9Wi5SfW1qIpYP2mELJ9CWglwnnxEC', 100000006, 'USER'),
       ('test_user_7@mail.ru', 'u7FirstName', 'u7LastName', '@testuser7',
        '$2a$08$b0uZZ4Du/09X9/r4h40GuO5PScb/UovpTj39FhvBTVucs5tXIR2K2', 100000007, 'USER'),
       ('test_user_8@gmail.com', 'u8FirstName', 'u8LastName', '@testuser8',
        '$2a$08$DP2eIF6sUTVpM7v81s94XuGF0TvE3n71P0cdARrUMyqq7uT13SBRO', 100000008, 'USER'),
       ('test_user_9@ya.ru', 'u9FirstName', 'u9LastName', '@testuser9',
        '$2a$08$jVuvpFt0snr0mueWtTCy4.INxzamkyx6KbFGjEatke5iifPP49Rga', 100000009, 'USER');

INSERT INTO courses (course_title, description, course_author_id, is_active)
VALUES ('JavaCore', 'First course', 1, true),
       ('Databases', 'Fundamentals of relation databases', 2, false),
       ('SQL', 'Queries for dummies', 4, true),
       ('Spring', 'Invert control', 5, true),
       ('Hibernate', 'Popular framework course', 2, true),
       ('Docker', 'Containers meltdown', 5, true),
       ('JUnit', 'Testing framework', 4, false),
       ('CI/CD', 'For devops', 1, true),
       ('Kafka', 'Messaging for noobs', 2, true),
       ('Algorithms', 'Introduction to algos and ds', 4, true);

INSERT INTO modules (module_title, module_number, module_content, id_course)
VALUES ('Введение в Java', 1, 'Основные концепции, история Java, установка JDK', 1),
       ('Синтаксис и типы данных', 2, 'Переменные, примитивные типы, операторы', 1),
       ('ООП в Java', 3, 'Классы, объекты, наследование, полиморфизм', 1),
       ('Исключения и ввод-вывод', 4, 'Try-catch, файловые операции, потоки', 1),
       ('Коллекции', 5, 'List, Set, Map, алгоритмы коллекций', 1),

       ('Введение в БД', 1, 'Что такое базы данных, виды БД, ACID', 2),
       ('Реляционная модель', 2, 'Таблицы, связи, нормализация', 2),
       ('Проектирование БД', 3, 'ER-диаграммы, выбор СУБД', 2),

       ('Основы SQL', 1, 'SELECT, FROM, WHERE, ORDER BY', 3),
       ('Агрегация данных', 2, 'GROUP BY, HAVING, агрегатные функции', 3),
       ('JOIN операции', 3, 'INNER, LEFT, RIGHT, FULL JOIN', 3),
       ('Подзапросы и CTE', 4, 'Вложенные запросы, общие табличные выражения', 3),
       ('Оконные функции', 5, 'Аналитические функции OVER()', 3),
       ('Оптимизация запросов', 6, 'Индексы, план выполнения', 3),

       ('Введение в Spring', 1, 'IoC, DI, Spring Context', 4),
       ('Spring Boot', 2, 'Автоконфигурация, стартеры', 4),
       ('Spring MVC', 3, 'Контроллеры, REST API', 4),
       ('Spring Data JPA', 4, 'Работа с БД через репозитории', 4),

       ('Основы Hibernate', 1, 'Сущности, сессии, конфигурация', 5),
       ('Маппинг объектов', 2, 'Аннотации, связи между сущностями', 5),
       ('Типы данных и конвертеры', 3, 'Пользовательские типы, конвертация', 5),
       ('HQL и Criteria API', 4, 'Язык запросов Hibernate', 5),
       ('Кэширование', 5, 'Уровни кэширования, стратегии', 5),
       ('Оптимизация производительности', 6, 'N+1 проблема, lazy loading', 5),
       ('Транзакции и блокировки', 7, 'Уровни изоляции, пессимистичные блокировки', 5),

       ('Основы контейнеризации', 1, 'Контейнеры, образы, Docker Hub', 6),
       ('Docker на практике', 2, 'Dockerfile, Docker Compose, деплой', 6),

       ('Основы тестирования', 1, 'JUnit 5, аннотации, assertions', 7),
       ('Продвинутое тестирование', 2, 'Mockito, интеграционные тесты', 7),
       ('Test Containers', 3, 'Тестирование с реальными сервисами', 7),

       ('Концепции CI/CD', 1, 'Непрерывная интеграция и доставка', 8),
       ('Jenkins Pipeline', 2, 'Настройка pipelines, джобы', 8),
       ('GitLab CI/CD', 3, '.gitlab-ci.yml, runners', 8),
       ('GitHub Actions', 4, 'Workflows, jobs, actions', 8),
       ('Деплоймент стратегии', 5, 'Blue-green, canary, rolling updates', 8),

       ('Архитектура Kafka', 1, 'Брокеры, топики, партиции', 9),
       ('Producer/Consumer API', 2, 'Настройка производителей и потребителей', 9),
       ('Kafka Connect и Streams', 3, 'Интеграция и обработка потоков', 9),
       ('Безопасность и мониторинг', 4, 'SSL, SASL, метрики, тюнинг', 9),

       ('Анализ сложности', 1, 'Big O, Omega, Theta нотации', 10),
       ('Базовые структуры данных', 2, 'Массивы, связные списки, стеки, очереди', 10),
       ('Деревья', 3, 'Бинарные деревья, BST, AVL, красно-черные', 10),
       ('Графы', 4, 'Представление графов, обходы', 10),
       ('Алгоритмы сортировки', 5, 'Quicksort, mergesort, heapsort', 10),
       ('Алгоритмы поиска', 6, 'Бинарный поиск, BFS, DFS, Дейкстра', 10),
       ('Динамическое программирование', 7, 'Мемоизация, типовые задачи', 10),
       ('Жадные алгоритмы', 8, 'Принципы, примеры применения', 10);

INSERT INTO course_tag_link (id_course, id_tag)
VALUES (1, 3),   -- java
       (1, 1),   -- backend
       (1, 14),  -- algorithms
       (1, 31),  -- design patterns

       (2, 6),   -- database
       (2, 1),   -- backend
       (2, 5),   -- system analysis

       (3, 6),   -- database
       (3, 1),   -- backend

       (4, 3),   -- java
       (4, 23),  -- spring
       (4, 1),   -- backend
       (4, 31),  -- design patterns

       (5, 3),   -- java
       (5, 24),  -- hibernate
       (5, 6),   -- database
       (5, 1),   -- backend

       (6, 7),   -- devops
       (6, 11),  -- cloud
       (6, 1),   -- backend

       (7, 3),   -- java
       (7, 9),   -- testing

       (8, 7),   -- devops
       (8, 1),   -- backend
       (8, 11),  -- cloud

       (9, 1),   -- backend
       (9, 7),   -- devops
       (9, 5),   -- system analysis
       (9, 13),  -- data science

       (10, 14), -- algorithms
       (10, 15), -- python
       (10, 16), -- c++
       (10, 3),  -- java
       (10, 13), -- data science
       (10, 31); -- design patterns

INSERT INTO user_course_access (user_id, course_id, access_granted_by)
VALUES (3, 1, 1),
       (3, 3, 4),
       (6, 1, 1),
       (6, 4, 5),
       (6, 5, 2),
       (7, 1, 1);

INSERT INTO user_module_access (user_id, course_id, module_id, access_granted_by)
VALUES (3, 1, 1, 1),
       (3, 1, 2, 1),
       (3, 4, 1, 5),
       (6, 1, 1, 1),
       (6, 1, 2, 1),
       (6, 1, 3, 1),
       (6, 3, 9, 4);

INSERT INTO mentor_time_slots (mentor_id, start_time, end_time, slot_type, slot_meeting_type,
                               max_participants, meeting_link, description, is_active)
VALUES (4, '2025-12-16 09:00:00', '2025-12-16 10:00:00', 'INDIVIDUAL', 'ACQUAINTANCE', 1,
        'https://meet.google.com/abc-defg-hij', 'Знакомство и обсуждение целей', true),
       (4, '2025-12-21 11:00:00', '2025-12-21 12:00:00', 'INDIVIDUAL', 'COMMUNICATION', 1,
        'https://meet.google.com/klm-nopq-rst', 'Разбор домашнего задания по Java', true),
       (4, '2025-12-25 14:00:00', '2025-12-25 15:30:00', 'GROUP', 'ACCEPTING', 5,
        'https://meet.google.com/uvw-xyz-123', 'Прием проектов по Spring Boot', true),

       (2, '2025-12-16 08:00:00', '2025-12-16 09:00:00', 'INDIVIDUAL', 'ACQUAINTANCE', 1,
        'https://meet.google.com/vvv-www-xxx', 'Вводная встреча', true),
       (2, '2025-12-21 10:00:00', '2025-12-21 12:00:00', 'GROUP', 'ACCEPTING', 6,
        'https://meet.google.com/yyy-zzz-aaa', 'Прием работ по базам данных', true),
       (2, '2025-12-25 14:00:00', '2025-12-25 16:00:00', 'GROUP', 'COMMUNICATION', 8,
        'https://meet.google.com/bbb-ccc-ddd', 'Q&A сессия по Kafka', true),

       (1, '2025-12-19 10:00:00', '2025-12-19 11:00:00', 'INDIVIDUAL', 'ACQUAINTANCE', 1,
        'https://meet.google.com/mmm-nnn-ooo', 'Знакомство и оценка уровня', true);

INSERT INTO mentor_time_slot__users (time_slot_id, user_id)
VALUES (7, 7),
       (1, 3),
       (2, 6),
       (5, 3),
       (5, 6);

INSERT INTO sent_notification (notification_type, recipient_id, notification_status, notification_destination, error_text)
VALUES
    ('COURSE_ACCESS_GRANTED', 3, 'OK', 'TELEGRAM', null),
    ('COURSE_ACCESS_GRANTED', 3, 'ERROR', 'EMAIL', 'test exception text'),
    ('SLOT_BOOKED_MENTOR', 2, 'OK', 'EMAIL', null),
    ('SLOT_BOOKED_MENTOR', 2, 'ERROR', 'TELEGRAM', 'test exception text');