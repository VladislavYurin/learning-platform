package ru.mentor.service;

import java.util.List;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;

/**
 * Интерфейс для работы с прогрессом учащихся в курсах.
 * Предоставляет методы для получения прогресса курса и списка учащихся,
 * участвующих в курсе под руководством определенного наставника.
 */
public interface ProgressService {

    CourseProgressResponse getCourseProgressByMentor(Long mentorId, Long courseId);

    List<MenteeProgressDto> getAllUsersAtCourse(Long mentorId, Long courseId);

}
