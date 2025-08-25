package ru.mentor.services;

import org.springframework.data.domain.Page;
import ru.mentor.dto.CourseDto;

/**
 * Сервис редиректа в админский сервис управления курсами.
 */
public interface RedirectAdminCourseService {

    /**
     * Возвращает курс по идентификатору.
     * @param courseId идентификатор курса
     * @return найденный курс
     */
    CourseDto getCourseById(Long courseId);

    /**
     * Возвращает список всех курсов.
     *
     * @param pageNumber
     *         номер страницы
     *
     * @param pageSize
     *         размер страницы
     *
     * @return список курсов
     */
    Page<CourseDto> getAllCourses(int pageNumber, int pageSize);

}
