package ru.mentor.testUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CommonTestUtil {

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    /**
     * Создание в БД юзера для интеграционного теста
     *
     * @param user
     *         Юзер.
     *
     * @return Сохраненный в БД юзер.
     */
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }

    /**
     * Создание в БД курса для интеграционного теста
     *
     * @param title
     *         Название курса
     * @param description
     *         Описание курса.
     * @param author
     *         Автор курса.
     *
     * @return Сохраненный в БД курс.
     */
    public CourseEntity createCourse(String title, String description, UserEntity author) {
        CourseEntity course = CourseEntity.builder()
                                          .courseTitle(title)
                                          .description(description)
                                          .isActive(TestConstantHolder.isActiveTrue)
                                          .author(author)
                                          .build();
        return courseRepository.save(course);
    }

    /**
     * Создание в БД модуля для интеграционного теста
     *
     * @param title
     *         Название модуля.
     * @param course
     *         Курс, к которому относится данный модуль.
     *
     * @return Сохраненный в БД модуль
     */
    public ModuleEntity createModule(String title, CourseEntity course) {
        ModuleEntity module = ModuleEntity.builder()
                                          .moduleTitle(title)
                                          .moduleOrderNumber(1)
                                          .moduleContent("<h1>" + TestConstantHolder.moduleContent + "</h1>")
                                          .isActive(TestConstantHolder.isActiveTrue)
                                          .course(course)
                                          .build();
        return moduleRepository.save(module);
    }

}
