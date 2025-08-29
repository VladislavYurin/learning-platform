package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.services.RedirectProgressService;

/**
 * Контроллер для отслеживания прогресса прохождения курсов.
 * Предоставляет endpoints для получения статистики по прогрессу прохождения курса и списка учеников в текущем модуле.
 */
@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Learning Progress Management", description = "Получение процесса обучения учеников")
public class ProgressController {

    private final RedirectProgressService redirectProgressService;

    @Operation(
            summary = "Получить прогресс по курсу",
            description = "Позволяет получить прогресс обучения в курсе автором этого курса. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информации о прогрессе учеников",
                            content = @Content(schema = @Schema(implementation = CourseProgressResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/course/{courseId}/statistics")
    public ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(@PathVariable Long courseId) {
        CourseProgressResponse response = redirectProgressService.getCourseProgressByMentor(courseId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Получить всех учеников в курсе",
            description = "Позволяет получить информацию о всех учениках в курсе с текущим модулем. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информацию о всех учениках в курсе",
                            content = @Content(schema = @Schema(allOf = MenteeProgressDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/course/{courseId}/users")
    public ResponseEntity<List<MenteeProgressDto>> getAllUsersAtCourse(@PathVariable Long courseId) {
        List<MenteeProgressDto> response = redirectProgressService.getAllUsersAtCourse(courseId);
        return ResponseEntity.ok().body(response);
    }
}
