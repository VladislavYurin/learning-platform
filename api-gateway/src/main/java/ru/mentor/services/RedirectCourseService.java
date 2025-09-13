package ru.mentor.services;

import java.util.List;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;

public interface RedirectCourseService {

    CourseDto createCourse(CreateCourseRequest request);

    void deleteCourse(Long courseId);

    CourseDto getCourseById(Long courseId);

    List<CourseDto> getAllActiveCourses();

    List<CourseDto> getAllCourses();

}
