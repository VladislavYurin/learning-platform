package ru.mentor.services;

import java.util.List;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;

/**
 * Сервис редиректов/агрегации прогресса по курсам.
 * Инкапсулирует обращение к внешнему сервису прогресса и скрывает детали интеграции.
 */
public interface RedirectProgressService {

    /**
     * Возвращает агрегированную статистику прогресса по курсу для текущего наставника.
     * @param courseId идентификатор курса
     * @return сводный ответ по прогрессу курса
     */
    CourseProgressResponse getCourseProgressByMentor(Long courseId);

    /**
     * Возвращает список прогресса всех обучающихся на указанном курсе (у текущего наставника).
     * @param courseId идентификатор курса
     * @return список DTO с прогрессом по каждому обучающемуся
     */
    List<MenteeProgressDto> getAllUsersAtCourse(Long courseId);

}
