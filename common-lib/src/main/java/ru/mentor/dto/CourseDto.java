package ru.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи информации о курсе.
 * Содержит основные данные о курсе.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о курсе")
public class CourseDto {

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

    /**
     * Список модулей, входящих в состав курса.
     */
    @Schema(description = "Список модулей курса")
    private List<ModuleDto> modules;

}
