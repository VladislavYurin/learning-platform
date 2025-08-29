package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.services.RedirectTagService;
import java.util.List;

@RestController
@RequestMapping("/course-tag")
@RequiredArgsConstructor
public class TagController {

    private final RedirectTagService redirectTagService;


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
       return redirectTagService.getAllTags();
    }
}
