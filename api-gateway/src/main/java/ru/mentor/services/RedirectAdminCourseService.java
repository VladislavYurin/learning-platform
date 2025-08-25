package ru.mentor.services;

import org.springframework.data.domain.Page;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.PageSettings;

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
     * @return список курсов
     */
    Page<CourseDto> getAllCourses(PageSettings pageSettings);

}
