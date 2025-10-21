package ru.mentor.service;

import java.util.List;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.CourseDtoWithoutModules;
import ru.mentor.dto.InnerCreateCourseRequest;

/**
 * Сервис для управления курсами в системе управления онлайн-курсами.
 * Интерфейс предоставляет методы для создания, удаления и получения информации о курсах.
 */
public interface CourseService {

    CourseDto createCourse(InnerCreateCourseRequest request);

    void deleteCourse(Long userId, Long courseId);

    List<CourseDto> getAllActiveCourses(Long userId);

    List<CourseDto> getAllCourses(Long userId);

    CourseDto getCourseById(Long userId, Long courseId);

    List<CourseDtoWithoutModules> getAllActiveCoursesPreview();
}
