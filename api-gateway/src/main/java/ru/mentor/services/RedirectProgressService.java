package ru.mentor.services;

import ru.mentor.dto.CourseProgressResponse;

public interface RedirectProgressService {

    CourseProgressResponse getCourseProgressByMentor(Long courseId);

}
