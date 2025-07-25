package ru.mentor.service;

import java.util.List;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;

public interface ProgressService {

    CourseProgressResponse getCourseProgressByMentor(Long mentorId, Long courseId);

    List<MenteeProgressDto> getAllUsersAtCourse(Long mentorId, Long courseId);

}
