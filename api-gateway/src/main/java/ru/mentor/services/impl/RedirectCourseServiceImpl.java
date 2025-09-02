package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.CourseClient;
import ru.mentor.mapper.CourseMapper;
import ru.mentor.services.RedirectCourseService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

import java.util.List;

/**
 * Реализация сервиса редиректов/интеграции для операций с курсами.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedirectCourseServiceImpl implements RedirectCourseService {

    private final UserService userService;

    private final CourseClient courseClient;

    private final CourseMapper courseMapper;

    /**
     * Создает новый курс.
     * @param request данные для создания курса
     * @return созданный модуль
     */
    @Override
    public CourseDto createCourse(CreateCourseRequest request) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на создание курса юзером [ ID = %d ].",
                rqUId,
                user.getId()
        ));
        return courseClient.createCourse(rqUId, courseMapper.mapToInnerCreateCourseRequest(
                user.getId(),
                request
        ));
    }

    /**
     * Удаляет курс.
     * @param courseId идентификатор курса
     * @return пустой ответ со статусом 200
     */
    @Override
    public ResponseEntity<?> deleteCourse(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на удаление курса [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                courseId,
                user.getId()
        ));
        return courseClient.deleteCourse(rqUId, user.getId(), courseId);
    }

    /**
     * Возвращает курс по идентификатору.
     * @param courseId идентификатор курса
     * @return найденный курс
     */
    @Override
    public CourseDto getCourseById(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение курса [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                courseId,
                user.getId()
        ));
        return courseClient.getCourseById(rqUId, user.getId(), courseId);
    }

    /**
     * Возвращает список всех активных курсов.
     * @return список курсов
     */
    @Override
    public List<CourseDto> getAllActiveCourses() {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение активных курсов юзером [ ID = %d ].",
                rqUId,
                user.getId()
        ));
        return courseClient.getAllActiveCourses(rqUId, user.getId());
    }

    /**
     * Возвращает список всех курсов.
     * @return список курсов
     */
    @Override
    public List<CourseDto> getAllCourses() {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение всех курсов юзером [ ID = %d ].",
                rqUId,
                user.getId()
        ));
        return courseClient.getAllCourses(rqUId, user.getId());
    }

}
