package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.services.RedirectCourseService;

/**
 * Контроллер для управления курсами и их содержимым.
 * Предоставляет endpoints для создания, получения и управления курсами.
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "Управление курсами и их содержимым")
public class CourseController {

    private final RedirectCourseService redirectCourseService;

    /**
     * Создает новый курс
     *
     * @param request
     *         Данные для создания курса
     *
     * @return Созданный курс
     */
    @Operation(
            summary = "Создать курс",
            description = "Позволяет создать новый курс. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Курс успешно создан",
                            content = @Content(schema = @Schema(implementation = CourseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(redirectCourseService.createCourse(request));
    }

    /**
     * Удаляет курс по идентификатору
     *
     * @param courseId
     *         Идентификатор курса
     *
     * @return Пустой ответ со статусом 200
     */
    @Operation(
            summary = "Удалить курс",
            description = "Позволяет удалить курс. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Курс успешно удален"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Курс не найден")
            }
    )
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        return redirectCourseService.deleteCourse(courseId);
    }

    /**
     * Получает курс по идентификатору
     *
     * @param courseId
     *         Идентификатор курса
     *
     * @return Найденный курс
     */
    @Operation(
            summary = "Получить курс по ID",
            description = "Возвращает информацию о курсе по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о курсе",
                            content = @Content(schema = @Schema(implementation = CourseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Курс не найден")
            }
    )
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(redirectCourseService.getCourseById(courseId));
    }

    /**
     * Получает список всех активных курсов
     *
     * @return Список активных курсов
     */
    @Operation(
            summary = "Получить активные курсы",
            description = "Возвращает список всех активных курсов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список активных курсов",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
            }
    )
    @GetMapping("/all/active")
    public ResponseEntity<List<CourseDto>> getAllActiveCourses() {
        return ResponseEntity.ok().body(redirectCourseService.getAllActiveCourses());
    }

    /**
     * Получает список всех курсов (включая неактивные)
     *
     * @return Список всех курсов
     */
    @Operation(
            summary = "Получить все курсы",
            description = "Возвращает список всех курсов, включая неактивные",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список всех курсов",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok().body(redirectCourseService.getAllCourses());
    }

}
