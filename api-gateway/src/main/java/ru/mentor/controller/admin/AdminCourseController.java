package ru.mentor.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseDto;
import ru.mentor.services.RedirectAdminCourseService;

/**
 * Контроллер управления курсами для администратора.
 */
@RestController
@RequestMapping("/admin/course")
@RequiredArgsConstructor
@Tag(name = "Admin Course Controller", description = "Управление курсами и их содержимым для админов.")
class AdminCourseController {

    private final RedirectAdminCourseService redirectAdminCourseService;

    /**
     * Возвращает курс по его ID
     *
     * @param courseId
     *         ID курса
     *
     * @return {@link CourseDto}
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
        return ResponseEntity.ok().body(redirectAdminCourseService.getCourseById(courseId));
    }

    /**
     * Возвращает страницу курсов
     *
     * @param pageNumber
     *         номер страницы
     *
     * @param pageSize
     *         размер страницы
     *
     * @return возвращает объект {@link Page}, содержащий объекты {@link CourseDto}
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
    public ResponseEntity<Page<CourseDto>> getAllCourses(
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        return ResponseEntity.ok().body(redirectAdminCourseService.getAllCourses(pageNumber, pageSize));
    }

}
