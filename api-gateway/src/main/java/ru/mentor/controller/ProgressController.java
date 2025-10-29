package ru.mentor.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.ProgressControllerApi;
import ru.mentor.gateway.model.CourseProgressResponse;
import ru.mentor.gateway.model.MenteeProgressDto;
import ru.mentor.mapper.CourseProgressResponseMapper;
import ru.mentor.mapper.MenteeProgressDtoMapper;
import ru.mentor.services.RedirectProgressService;

/**
 * Контроллер для отслеживания прогресса прохождения курсов.
 * Предоставляет endpoints для получения статистики по прогрессу прохождения курса и списка учеников в текущем модуле.
 */
@RestController
@RequiredArgsConstructor
public class ProgressController implements ProgressControllerApi {

    private final RedirectProgressService redirectProgressService;
    private final MenteeProgressDtoMapper menteeProgressDtoMapper;
    private final CourseProgressResponseMapper courseProgressResponseMapper;

    /**
     * Реализация ручки GET /progress/course/{courseId}/users
     */
    @Override
    public ResponseEntity<List<MenteeProgressDto>> getAllUsersAtCourse(Long courseId) {
        List<ru.mentor.dto.MenteeProgressDto> response = redirectProgressService.getAllUsersAtCourse(courseId);
        List<MenteeProgressDto> listApiMenteeProgressDto = menteeProgressDtoMapper.toApiList(response);
        return ResponseEntity.ok(listApiMenteeProgressDto);
    }

    /**
     * Реализация ручки GET /progress/course/{courseId}/statistics
     */
    @Override
    public ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(Long courseId) {
        ru.mentor.dto.CourseProgressResponse response = redirectProgressService.getCourseProgressByMentor(courseId);
        CourseProgressResponse apiCourseProgressResponse = courseProgressResponseMapper.toApi(response);
        return ResponseEntity.ok(apiCourseProgressResponse);
    }
}
