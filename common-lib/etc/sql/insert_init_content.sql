insert into courses(id_course, course_title, description, is_active, course_author_id, created_at)
values (1, 'Java-разработчик с нуля',
        'Изучение теории и практики для трудоустройства с нулевым опытом', true, 2, now());

insert into modules(id_module, module_title, module_number, module_content, id_course, is_active,
                    created_at)
values (1, 'Core 1', 1, '<h1>Тест</h1>
<h2>🐺 Привет 🐺</h2>
<h3>Тест1.</h3>', 1, true, now()),
       (2, 'Core 1', 2, '<h1>Еще один тест</h1>
<h2>🐺 Привет 🐺</h2>
<h3>Еще тест2</h3>', 1, true, now())