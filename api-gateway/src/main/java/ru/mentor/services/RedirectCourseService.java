package ru.mentor.services;

import java.util.List;
import org.springframework.http.ResponseEntity;
import ru.mentor.dto.CourseDto;
import ru.mentor.gateway.model.CreateCourseRequest;

/**
 * Сервис редиректов/интеграции для операций с курсами.
 * <p>
 *     Инкапсулирует вызовы к внешнему сервису курсов и скрывает детали транспорта
 *     и авторизации. Используется контроллерами для создания, получения и удаления курсов,
 *     а также для выборок активных и всех курсов.
 * </p>
 */
public interface RedirectCourseService {

    /**
     * Создаёт новый курс.
     * @param request данные для создания курса
     * @return созданный курс
     */
    CourseDto createCourse(CreateCourseRequest request);

    /**
     * Удаляет курс.
     * @param courseId идентификатор курса
     * @return пустой ответ со статусом 200
     */
    ResponseEntity<?> deleteCourse(Long courseId);

    /**
     * Возвращает курс по идентификатору.
     * @param courseId идентификатор курса
     * @return найденный курс
     */
    CourseDto getCourseById(Long courseId);

    /**
     * Возвращает список всех активных курсов.
     * @return список курсов
     */
    List<CourseDto> getAllActiveCourses();

    /**
     * Возвращает список всех курсов.
     * @return список курсов
     */
    List<CourseDto> getAllCourses();

}
