package ru.mentor.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CourseTagDto {

    @Schema(description = "ID тэга", example = "1")
    private Long id;

    @Schema(description = "Название тэга", example = "backend")
    private String tagName;

    @Schema(description = "Дата создание тэга", example = "2023-01-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Флаг активности тэга", example = "true")
    private Boolean isActive;

}
