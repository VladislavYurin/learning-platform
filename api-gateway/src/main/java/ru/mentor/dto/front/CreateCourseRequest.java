package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для запроса создания нового курса.
 * Содержит основную информацию, необходимую для создания курса:
 * название и описание.
 * 
 * @author API Gateway Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@Schema(description = "Запрос на создание курса")
public class CreateCourseRequest {

    /**
     * Название курса.
     * Должно быть уникальным и описательным.
     */
    @Schema(description = "Название курса", example = "Основы Spring Boot")
    @NotBlank
    private String courseName;

    /**
     * Подробное описание курса.
     * Содержит информацию о содержании, целях и требованиях курса.
     */
    @Schema(description = "Описание курса", example = "Курс по основам Spring Framework")
    @NotBlank
    private String courseDescription;

}
