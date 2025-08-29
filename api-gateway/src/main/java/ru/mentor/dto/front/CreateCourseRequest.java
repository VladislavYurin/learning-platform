package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Запрос на создание курса")
public class CreateCourseRequest {

    /**
     * Идентификатор автора курса.
     */
    private Long authorId;

    @Schema(description = "Название курса", example = "Основы Spring Boot")
    @NotBlank
    private String courseName;

    @Schema(description = "Описание курса", example = "Курс по основам Spring Framework")
    @NotBlank
    private String courseDescription;

    private List<Long> tagIds;

}
