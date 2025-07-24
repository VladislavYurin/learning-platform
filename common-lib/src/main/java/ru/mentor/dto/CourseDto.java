package ru.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Информация о курсе")
public class CourseDto {

    @Schema(description = "ID курса", example = "1")
    private Long id;

    @Schema(description = "Название курса", example = "Основы Spring Boot")
    private String courseTitle;

    @Schema(description = "Описание курса", example = "Курс по основам Spring Framework")
    private String courseDescription;

    @Schema(description = "Флаг активности курса", example = "true")
    private Boolean isActive;

    @Schema(description = "Дата создания курса", example = "2023-01-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "ID автора курса", example = "5")
    private Long authorId;

    @Schema(description = "Список модулей курса")
    private List<ModuleDto> modules;

}
