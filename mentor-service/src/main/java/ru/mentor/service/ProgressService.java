package ru.mentor.service;

import ru.mentor.dto.CourseProgressResponse;

public interface ProgressService {

    CourseProgressResponse getCourseProgressByMentor(Long mentorId, Long courseId);

}
