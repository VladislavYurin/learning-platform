package ru.mentor.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCourseTagRequest {

    private Long id;

    @Schema(description = "Название тэга", example = "backend")
    @NotBlank
    private String tagName;

    private LocalDateTime createdAt;

    private Boolean isActive;
}
