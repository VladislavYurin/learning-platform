package ru.mentor.service;

import java.util.List;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.InnerCreateCourseRequest;

public interface CourseService {

    CourseDto createCourse(InnerCreateCourseRequest request);

    void deleteCourse(Long userId, Long courseId);

    List<CourseDto> getAllActiveCourses(Long userId);

    List<CourseDto> getAllCourses(Long userId);

    CourseDto getCourseById(Long userId, Long courseId);

}
