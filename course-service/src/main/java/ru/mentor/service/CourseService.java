package ru.mentor.service;

import java.util.List;
import ru.mentor.dto.Course;
import ru.mentor.dto.InnerCreateCourseRequest;

public interface CourseService {

    Course createCourse(InnerCreateCourseRequest request);

    void deleteCourse(Long userId, Long courseId);

    List<Course> getAllActiveCourses();

    List<Course> getAllCourses();

    Course getCourseById(Long courseId);

}
