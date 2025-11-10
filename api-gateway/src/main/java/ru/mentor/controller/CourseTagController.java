package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.services.RedirectCourseTagService;

@RestController
@RequestMapping("/course-tag")
@RequiredArgsConstructor
public class CourseTagController {

    private final RedirectCourseTagService redirectCourseTagService;

    @Operation(
            summary = "Создать тэг для курсов",
            description = "Позволяет создать новый тэг для курсов. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Тег успешно создан",
                            content = @Content(schema = @Schema(implementation = CourseTagDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<CourseTagDto> createTag(@RequestBody CreateCourseTagRequest request) {
        return ResponseEntity.ok(redirectCourseTagService.createCourseTag(request));
    }

    @Operation(
            summary = "Удалить тег",
            description = "Позволяет удалить тег для курсов. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Тег успешно удален"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            }
    )
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        redirectCourseTagService.deleteCourseTag(tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получить тег по ID",
            description = "Возвращает информацию о теге для курсов по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о теге для курсов",
                            content = @Content(schema = @Schema(implementation = CourseTagDto.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            }
    )
    @GetMapping("/{tagId}")
    public ResponseEntity<CourseTagDto> getTagById(@PathVariable Long tagId) {
        return ResponseEntity.ok().body(redirectCourseTagService.getTagById(tagId));
    }

    @Operation(
            summary = "Получить все теги курсов",
            description = "Возвращает список всех тегов курсов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список всех тегов курсов",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseTagDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<CourseTagDto>> getAllTags() {
       return ResponseEntity.ok().body(redirectCourseTagService.getAllTags());
    }
}
