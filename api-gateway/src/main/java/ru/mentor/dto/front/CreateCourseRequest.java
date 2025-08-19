package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Класс для создания запроса на создание курса.
 * Используется как тело HTTP-запроса при создании/импорте курса.
 */
@Data
@Builder
@Schema(description = "Запрос на создание курса")
public class CreateCourseRequest {

    @Schema(description = "Название курса", example = "Основы Spring Boot")
    @NotBlank
    private String courseName;

    @Schema(description = "Описание курса", example = "Курс по основам Spring Framework")
    private String courseDescription;

}
