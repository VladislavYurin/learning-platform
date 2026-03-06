package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MentorTagDto {

    @Schema(description = "ID тэга ментора", example = "1")
    private Long id;

    @Schema(description = "Название тэга ментора", example = "backend")
    private String mentorTagName;

    @Schema(description = "Дата создание тэга ментора", example = "2023-01-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Флаг активности тэга ментора", example = "true")
    private Boolean isActive;

}
