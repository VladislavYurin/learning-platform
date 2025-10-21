package ru.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.mentor.dto.tag.CourseTagDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для передачи информации о курсе без модулей.
 * Содержит основные данные о курсе.
 */
@Data
@Builder
@Schema(description = "Информация о курсе")
public class CourseDtoWithoutModules {

    /**
     * Уникальный идентификатор курса.
     */
    @Schema(description = "ID курса", example = "1")
    private Long id;

    /**
     * Название курса.
     */
    @Schema(description = "Название курса", example = "Основы Spring Boot")
    private String courseTitle;

    /**
     * Описание курса.
     */
    @Schema(description = "Описание курса", example = "Курс по основам Spring Framework")
    private String courseDescription;

    /**
     * Флаг, указывающий на активность курса.
     */
    @Schema(description = "Флаг активности курса", example = "true")
    private Boolean isActive;

    /**
     * Дата и время создания курса.
     */
    @Schema(description = "Дата создания курса", example = "2023-01-15T10:00:00")
    private LocalDateTime createdAt;

    /**
     * Информация об авторе курса.
     */
    @Schema(description = "ID автора курса", example = "5")
    private UserInfoDto author;

    @Schema(description = "Тэги курса")
    private List<CourseTagDto> tags;
}
