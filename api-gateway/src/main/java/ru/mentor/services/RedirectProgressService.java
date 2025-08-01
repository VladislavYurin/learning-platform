package ru.mentor.services;

import java.util.List;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;

public interface RedirectProgressService {

    CourseProgressResponse getCourseProgressByMentor(Long courseId);

    List<MenteeProgressDto> getAllUsersAtCourse(Long courseId);

}
